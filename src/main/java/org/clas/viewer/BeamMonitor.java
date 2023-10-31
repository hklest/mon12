package org.clas.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.epics.ca.Channel;
import org.epics.ca.Context;

/**
 *
 * @author baltzell
 */
public class BeamMonitor extends Thread {

    static final String[] PVNAMES = {"IPM2C21A", "IPM2C24A"};
    static final float DEFAULT_BEAM_CURRENT = -1;

    private final List<Channel> beamCurrents = new ArrayList<>();
    private volatile float beamCurrent = DEFAULT_BEAM_CURRENT;
    private float minBeamCurrent = DEFAULT_BEAM_CURRENT;

    public BeamMonitor(float minimumBeamCurrent) {
        minBeamCurrent = minimumBeamCurrent;
    }

    public float getBeamCurrent() {
        return beamCurrent;
    }

    public boolean getBeamStatus() {
        return beamCurrents.isEmpty() || beamCurrent > minBeamCurrent;
    }

    @Override
    public void run() {

        Context context = new Context();

        // connect to all PVs:
        for (String s : PVNAMES) {
            Channel c = context.createChannel(s, Double.class);
            try {
                c.connectAsync().get(2, TimeUnit.SECONDS);
                beamCurrents.add(c);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                System.err.println(ex);
                System.err.println("Error connection to PV:  " + s);
            }
        }
        if (beamCurrents.isEmpty()) {
            System.err.println("No PVs available.  Assume beam is always good.");
        }
        
        // periodically poll the PVs:
        while (!beamCurrents.isEmpty()) {
            float minimum = Float.POSITIVE_INFINITY;
            for (Channel c : beamCurrents) {
                try {
                    minimum = (float) Math.min(minimum, (double)c.getAsync().get());
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println(ex);
                } 
            }

            beamCurrent = minimum!=Float.POSITIVE_INFINITY ? minimum : DEFAULT_BEAM_CURRENT;

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.err.println(ex);
            }
        }
    }
}
