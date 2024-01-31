package org.clas.detectors;

import org.clas.viewer.DetectorMonitor;
import org.jlab.detector.helicity.DecoderBoardUtil;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

/**
 *
 * @author devita
 */


public class HELmonitor extends DetectorMonitor {

    static final byte patternLength = 4;
    static final byte delayWindows = 8;
    
    public HELmonitor(String name) {
        super(name);
        this.setDetectorTabNames("signals","board");
        this.init(false);
    }

    @Override
    public void createHistos() {
        this.setNumberOfEvents(0);
        H1F summary = new H1F("summary","helicity",20,-1.5,1.5);
        summary.setTitleX("helicity");
        summary.setTitleY("Counts");
        summary.setFillColor(3);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setDetectorSummary(sum);
        H1F rawHel = new H1F("rawHelicity","rawHelicity", 200,0.,4000.);
        rawHel.setTitleX("helicity");
        rawHel.setTitleY("Counts");
        rawHel.setFillColor(22);
        H1F rawSync = new H1F("rawSync","rawSync", 200,0.,4000.);
        rawSync.setTitleX("sync");
        rawSync.setTitleY("Counts");
        rawSync.setFillColor(33);
        H1F rawQuartet = new H1F("rawQuartet","rawQuartet", 200,0.,4000.);
        rawQuartet.setTitleX("quartet");
        rawQuartet.setTitleY("Counts");
        rawQuartet.setFillColor(44);
        H1F hel = new H1F("helicity","helicity", 20,-1.5,1.5);
        hel.setTitleX("helicity");
        hel.setTitleY("Counts");
        hel.setFillColor(22);
        H1F sync = new H1F("sync","sync", 20,-1.5,1.5);
        sync.setTitleX("sync");
        sync.setTitleY("Counts");
        sync.setFillColor(33);
        H1F quartet = new H1F("quartet","quartet", 20,-1.5,1.5);
        quartet.setTitleX("quartet");
        quartet.setTitleY("Counts");
        quartet.setFillColor(44);
        GraphErrors  helSequence = new GraphErrors("helSequence");
        helSequence.setTitle("Helicity Sequence");
        helSequence.setTitleX("Event Number");
        helSequence.setTitleY("Signals");
        helSequence.setMarkerColor(22);
        helSequence.setMarkerSize(5);
        GraphErrors  syncSequence = new GraphErrors("syncSequence");
        syncSequence.setTitle("Sync Sequence");
        syncSequence.setTitleX("Event Number");
        syncSequence.setTitleY("Signals");
        syncSequence.setMarkerColor(33);
        syncSequence.setMarkerSize(5);
        GraphErrors  quartetSequence = new GraphErrors("quartetSequence");
        quartetSequence.setTitle("Quartet Sequence");
        quartetSequence.setTitleX("Event Number");
        quartetSequence.setTitleY("Signals");
        quartetSequence.setMarkerColor(44);
        quartetSequence.setMarkerSize(5);

        DataGroup dg = new DataGroup(2,4);
        dg.addDataSet(rawHel,      0);
        dg.addDataSet(rawSync,     1);
        dg.addDataSet(rawQuartet,  2);
        dg.addDataSet(hel,         4);
        dg.addDataSet(sync,        5);
        dg.addDataSet(quartet,     6);
        dg.addDataSet(helSequence, 7);
        dg.addDataSet(syncSequence, 7);
        dg.addDataSet(quartetSequence, 7);

        this.getDataGroup().add(dg, 0,0,0);
        
        DataGroup dg2 = new DataGroup(2,2);
        H1F template = new H1F("h","",3,-1.5,1.5);
        template.setTitleY("Counts");
        String[] signals = {"HelicityCorr","Helicity","Pair","Pattern"};
        int i=0;
        for (String s : signals) {
            H1F h = template.histClone(String.format("helbrd%s",s));
            h.setTitleX(s);
            h.setFillColor(3);
            dg2.addDataSet(h, i++);
        }
        this.getDataGroup().add(dg2, 1,0,0);
    }
       
    @Override
    public void plotHistos() {
        this.getDetectorCanvas().getCanvas("signals").divide(3, 2);
        this.getDetectorCanvas().getCanvas("signals").setGridX(false);
        this.getDetectorCanvas().getCanvas("signals").setGridY(false);
        this.getDetectorCanvas().getCanvas("signals").cd(0);
        this.getDetectorCanvas().getCanvas("signals").getPad(0).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("rawHelicity"));
        this.getDetectorCanvas().getCanvas("signals").cd(1);
        this.getDetectorCanvas().getCanvas("signals").getPad(1).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("rawSync"));
        this.getDetectorCanvas().getCanvas("signals").cd(2);
        this.getDetectorCanvas().getCanvas("signals").getPad(2).getAxisY().setLog(true);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("rawQuartet"));
        this.getDetectorCanvas().getCanvas("signals").cd(3);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("helicity"));
        this.getDetectorCanvas().getCanvas("signals").cd(4);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("sync"));
        this.getDetectorCanvas().getCanvas("signals").cd(5);
        this.getDetectorCanvas().getCanvas("signals").draw(this.getDataGroup().getItem(0,0,0).getH1F("quartet"));
        this.getDetectorCanvas().getCanvas("signals").update();

        this.getDetectorCanvas().getCanvas("board").divide(2,2);
        this.getDetectorCanvas().getCanvas("board").cd(0);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicityCorr"));
        this.getDetectorCanvas().getCanvas("board").cd(1);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicity"));
        this.getDetectorCanvas().getCanvas("board").cd(2);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdPair"));
        this.getDetectorCanvas().getCanvas("board").cd(3);
        this.getDetectorCanvas().getCanvas("board").draw(this.getDataGroup().getItem(1,0,0).getH1F("helbrdPattern"));
    }

    @Override
    public void processEvent(DataEvent event) {
        if (event.hasBank("HEL::decoder")) {
            DataBank b = event.getBank("HEL::decoder");
            int rows = b.rows();
            for (int i=0; i<rows; ++i) {

                boolean x = DecoderBoardUtil.checkPairs(b.getInt("pairArray",i));
                boolean y = DecoderBoardUtil.checkPatterns(b.getInt("patternArray",i), patternLength);
                boolean z = DecoderBoardUtil.checkHelicities(b.getInt("patternArray",i), b.getInt("helicityArray",i), patternLength);
                int h = x&&y&&z ? -1+2*DecoderBoardUtil.getWindowHelicity(
                    DecoderBoardUtil.getPatternHelicity(b.getInt("helicityPArray", i),
                    delayWindows/patternLength), delayWindows%patternLength) : 0;
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicityCorr").fill(h);
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdHelicity").fill(-1+2*(float)(b.getInt("helicityArray",i)&1));
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdPair").fill(-1+2*(float)(b.getInt("pairArray",i)&1));
                this.getDataGroup().getItem(1,0,0).getH1F("helbrdPattern").fill(-1+2*(float)(b.getInt("patternArray",i)&1));
            }
        }
        if (event.hasBank("RUN::trigger") && event.hasBank("RUN::config") && event.hasBank("HEL::adc")) {
            DataBank bank = event.getBank("HEL::adc");
            int rows = bank.rows();
            int hel     = -1;
            for(int loop = 0; loop < rows; loop++){
                int component = bank.getShort("component", loop);
                int rawValue  = bank.getShort("ped", loop);
                int value = (int) rawValue/2000;
                switch (component) {
                    case 1:
                        this.getDataGroup().getItem(0,0,0).getH1F("rawHelicity").fill(rawValue);
                        this.getDataGroup().getItem(0,0,0).getH1F("helicity").fill(value);
                        hel=value;
                        break;
                    case 2:
                        this.getDataGroup().getItem(0,0,0).getH1F("rawSync").fill(rawValue);
                        this.getDataGroup().getItem(0,0,0).getH1F("sync").fill(value);
                        break;
                    case 3:
                        this.getDataGroup().getItem(0,0,0).getH1F("rawQuartet").fill(rawValue);
                        this.getDataGroup().getItem(0,0,0).getH1F("quartet").fill(value);
                        break;
                    default:
                        break;
                }
            }
            this.getDetectorSummary().getH1F("summary").fill(hel);
        }       
    }

    @Override
    public void analysisUpdate() {

    }
    
}
