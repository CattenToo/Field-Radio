package arnett.fieldRadio;

import arnett.fieldRadio.Items.Radio.Radio;
import arnett.fieldRadio.Items.Radio.RadioVoiceChat;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.StaticAudioChannel;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.opus.OpusEncoder;
import de.maxhenkel.voicechat.api.packets.Packet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;

public class FieldRadioVoiceChat implements VoicechatPlugin {

    VoicechatApi api;
    OpusDecoder decoder;
    OpusEncoder encoder;


    @Override
    public String getPluginId() {
        return "FieldRadio";
    }

    @Override
    public void initialize(VoicechatApi api)
    {
        this.api = api;
        decoder = api.createDecoder();
        encoder = api.createEncoder();
    }

    @Override
    public void registerEvents(EventRegistration registration)
    {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
    }

    //whenever someone speaks
    public void onMicrophone(MicrophonePacketEvent e)
    {

        // The connection might be null if the event is caused by other means
        if (e.getSenderConnection() == null)
            return;

        // Cast the generic player object of the voice chat API to an actual bukkit player
        // This object should always be a bukkit player object on bukkit based servers
        if (!(e.getSenderConnection().getPlayer().getPlayer() instanceof Player player))
            return;

        //make sure player is actively holding a radio
        if(!Radio.isHoldingRadio(player))
            return;

        //grace period so voice doesn't cut off at end
        if(!player.hasActiveItem() && !RadioVoiceChat.playersInGracePeroid.containsKey(player.getUniqueId()))
        {
                return;
        }
        else if (player.hasActiveItem())
        {
            //update Grace period
            RadioVoiceChat.playersInGracePeroid.put(player.getUniqueId(), System.nanoTime() + Config.radio_gracePeriod);
        }
        else
        {
            //they aren't using the radio and are on grace
            if(RadioVoiceChat.playersInGracePeroid.get(player.getUniqueId()) < System.nanoTime())
            {
                //remove player from grace and stop packet
                RadioVoiceChat.removeFromGrace(player.getUniqueId());
                return;
            }
        }

        //send packets to others listening to frequency
        String frequency = Radio.getFrequency(Radio.getHeldRadio(player).get());

        //gets voice chat api for connections
        VoicechatServerApi serverVC = e.getVoicechat();

        // worst case scenario is someone is filling up a frequency with like 41 radios
        Set<UUID> processed = new HashSet<>((int)Math.sqrt(RadioVoiceChat.frequencyListeners.get(frequency).size()));

        //so player doesn't hear themselves
        processed.add(player.getUniqueId());

        for(UUID id : RadioVoiceChat.frequencyListeners.get(frequency))
        {
            //skip if already added to set (they've already been sent the packet)
            if(!processed.add(id))
            {
                continue;
            }

            //grab connection
            VoicechatConnection connection = serverVC.getConnectionOf(id);

            //make sure connection is there
            if(connection == null || !connection.isConnected())
                continue;

            byte[] audioData = e.getPacket().getOpusEncodedData();

            if(Config.radio_audioFilter_enabled)
            {
                //modify packet
                short[] decodedData = decoder.decode(audioData);

                // Filter states to maintain continuity across packets
                double lowPassState = 0;
                double highPassState = 0;
                double lastRawSample = 0;
                Random random = new Random();

                // Configuration constants
                double LP_ALPHA = Config.radio_audioFilter_LPAlpha; // Lower = more muffled
                double HP_ALPHA = Config.radio_audioFilter_HPAlpha; // Higher = less bass
                int NOISE_FLOOR = Config.radio_audioFilter_noiseFloor;  // Constant hiss volume
                int CRACKLE_CHANCE = Config.radio_audioFilter_crackleChance; // 1 in 2000 samples

                // no, I did not actually code the audio manipulation part of the filter since I'm not the best at working with audio

                for (int i = 0; i < decodedData.length; i++) {
                    double currentSample = decodedData   [i];

                    // 1. BANDPASS FILTER (EQ)
                    // Low Pass (Cuts highs)
                    lowPassState = LP_ALPHA * currentSample + (1 - LP_ALPHA) * lowPassState;
                    double filtered = lowPassState;

                    // High Pass (Cuts lows)
                    highPassState = HP_ALPHA * highPassState + HP_ALPHA * (filtered - lastRawSample);
                    lastRawSample = filtered;

                    // 2. SATURATION (The "Crunch")
                    // Convert to -1.0 to 1.0 range for math
                    double x = highPassState / 32768.0;
                    // Soft clipping formula: (3x - x^3) / 2
                    double saturated = (3 * x - Math.pow(x, 3)) / 2.0;

                    // 3. NOISE & INTERFERENCE
                    // Constant low-level hiss
                    int hiss = random.nextInt(NOISE_FLOOR * 2 + 1) - NOISE_FLOOR;

                    // Random electrical "crackles"
                    int crackle = (random.nextInt(CRACKLE_CHANCE) == 0) ? (random.nextInt(6000) - 3000) : 0;

                    // 4. CLAMP & OUTPUT
                    int finalSample = (int) (saturated * 32767) + hiss + crackle;
                    decodedData[i] = (short) Math.max(-32768, Math.min(32767, finalSample));
                }


                audioData = encoder.encode(decodedData);
            }

            FieldRadio.logger.info("Recived by " + Bukkit.getPlayer(id).getName());
            //send audio
            serverVC.sendStaticSoundPacketTo(connection, e.getPacket().staticSoundPacketBuilder().opusEncodedData(audioData).build());
        }
    }
}
