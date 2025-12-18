package arnett.fieldRadio;

import arnett.fieldRadio.Items.Radio.Radio;
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

    //stores all players that are listening to which frequency
    static HashMap<String, ArrayList<UUID>> frequencyListeners = new HashMap<>();
    //stores all players who are on grace period so mic doesn't cut off
    // uses long because I feel like it and ticks can slow which isn't really ideal for this
    static HashMap<UUID, Long> playersInGracePeroid = new HashMap<>();

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

        FieldRadio.logger.info("Microphone Packet sent to radio by " + player.getName());

        //make sure player is actively holding a radio
        if(!Radio.isHoldingRadio(player))
            return;

        //grace period so voice doesn't cut off at end
        if(!player.hasActiveItem() && !playersInGracePeroid.containsKey(player.getUniqueId()))
        {
                return;
        }
        else if (player.hasActiveItem())
        {
            //update Grace period
            playersInGracePeroid.put(player.getUniqueId(), System.nanoTime() + FieldRadio.singleton.getConfig().getLong(Config.radio_gracePeriod.path()));
        }
        else
        {
            //they aren't using the radio and are on grace
            if(playersInGracePeroid.get(player.getUniqueId()) < System.nanoTime())
            {
                //remove player from grace and stop packet
                removeFromGrace(player.getUniqueId());
                return;
            }
        }

        //send packets to others listening to frequency
        String frequency = Radio.getFrequency(Radio.getHeldRadio(player).get());

        //gets voice chat api for connections
        VoicechatServerApi serverVC = e.getVoicechat();

        FieldRadio.logger.info("Sent on  " + frequency);

        // worst case scenario is someone is filling up a frequency with like 41 radios
        Set<UUID> processed = new HashSet<>((int)Math.sqrt(frequencyListeners.get(frequency).size()));

        //so player doesn't hear themselves
        processed.add(player.getUniqueId());

        for(UUID id : frequencyListeners.get(frequency))
        {
            //skip if already added to set (they've already been sent the packet)
            if(!processed.add(id))
            {
                FieldRadio.logger.info("Player has already been processed:  " + player.getName());
                continue;
            }

            //grab connection
            VoicechatConnection connection = serverVC.getConnectionOf(id);

            //make sure connection is there
            if(connection == null || !connection.isConnected())
                continue;

            byte[] audioData = e.getPacket().getOpusEncodedData();

            if(FieldRadio.config.getBoolean(Config.radio_audioFilter_enabled.path()))
            {

                //modify packet
                short[] decodedData = decoder.decode(audioData);

                // Filter states to maintain continuity across packets
                double lowPassState = 0;
                double highPassState = 0;
                double lastRawSample = 0;
                Random random = new Random();

                // Configuration constants
                double LP_ALPHA = FieldRadio.config.getDouble(Config.radio_audioFilter_LPAlpha.path());  // Lower = more muffled
                double HP_ALPHA = FieldRadio.config.getDouble(Config.radio_audioFilter_HPAlpha.path()); // Higher = less bass
                int NOISE_FLOOR = FieldRadio.config.getInt(Config.radio_audioFilter_noiseFloor.path());  // Constant hiss volume
                int CRACKLE_CHANCE = FieldRadio.config.getInt(Config.radio_audioFilter_crackleChance.path()); // 1 in 2000 samples

                FieldRadio.logger.info(" " + LP_ALPHA + " " + + HP_ALPHA + " " + + NOISE_FLOOR + " " + + CRACKLE_CHANCE + " ");

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

    public static void addToFrequency(String frequency, UUID id)
    {
        frequencyListeners.computeIfAbsent(frequency, key -> new ArrayList<UUID>()).add(id);
    }

    public static void removeFromFrequency(String frequency, UUID id)
    {
        frequencyListeners.get(frequency).remove(id);

        if (frequencyListeners.get(frequency).isEmpty())
            frequencyListeners.remove(frequency);
    }

    public static void removeFromFrequency(UUID id)
    {
        frequencyListeners.entrySet().removeIf(((s) -> {
            s.getValue().removeIf((e) ->
                e.equals(id)
            );

            if (s.getValue().isEmpty())
                return true;
            return false;
        }));
    }

    //cleans frequencies
    public static void clearFrequencies()
    {
        frequencyListeners.clear();
    }

    //overload for clearing one player from frequencies
    public static void clearFrequencies(Player player)
    {
        UUID target = player.getUniqueId();

        //remove the player from the list and remove the list entry if it is empty
        frequencyListeners.entrySet().removeIf((entry) -> {
            entry.getValue().removeIf(k -> k.equals(target));
            if (entry.getValue().isEmpty())
                return true;
            return false;
        });
    }

    public static Map<String, ArrayList<UUID>> getFrequencys()
    {
        return Collections.unmodifiableMap(frequencyListeners);
    }

    public static void removeFromGrace(UUID id)
    {
        playersInGracePeroid.remove(id);
    }

    public static void refresh(Player target){

        FieldRadioVoiceChat.clearFrequencies(target);

        ItemStack[] radios = Radio.getRadiosFromPlayer(target);

        for(ItemStack radio : radios)
        {
            FieldRadioVoiceChat.addToFrequency(Radio.getFrequency(radio), target.getUniqueId());
        }
    }
}
