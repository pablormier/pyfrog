package es.usc.citius.pyfrog;

import frog.inference.TSKInference;
import frog.rulebase.KnowledgeBase;
import frog.rulebase.TSKRule;
import org.apache.commons.lang.ArrayUtils;
import org.yaml.snakeyaml.Yaml;
import py4j.GatewayServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class PyFrogEP {

    private Yaml yaml = new Yaml();

    class LoadedKB {
        private KnowledgeBase<TSKRule> kb;

        public LoadedKB(KnowledgeBase<TSKRule> kb) {
            this.kb = kb;
        }

        public List<Double> inference(List<Double> input){
            return toList(TSKInference.inference(this.kb, toPrimitive(input)));
        }

        public List<Double> normalizedInference(List<Double> input){
            return toList(TSKInference.denormalizedInference(this.kb, toPrimitive(input)));
        }

        public List<Double> denormalizedInference(List<Double> input){
            return toList(TSKInference.denormalizedInference(this.kb, toPrimitive(input)));
        }

        // Inference as applied by TskView (results differ from the ones returned by the other methods!)
        public double simpleInference(List<Double> inputs){
            // Contains the original input values
            double[] data = toPrimitive(inputs);
            // Contains the normalized input values
            double[] dataNorm = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                dataNorm[i] = (data[i] - kb.database.inputs[i].normMean) / kb.database.inputs[i].normStd;
            }
            double num = 0;
            // Inference per rule
            for (TSKRule rule : kb.rulebase) {
                // To calculate the output, the consequent weights must be multiplied by the normalized data
                // and the output must be denormalized
                num += (rule.inference(dataNorm)[0] * kb.database.outputs[0].normStd + kb.database.outputs[0].normMean) * rule.dof(data, kb.database);
            }
            return num;
        }

        public KnowledgeBase<TSKRule> getKnowledgeBase() {
            return kb;
        }

        private double[] toPrimitive(List<Double> input){
            return ArrayUtils.toPrimitive(input.toArray(new Double[input.size()]));
        }

        private List<Double> toList(double[] input){
            return Arrays.asList(ArrayUtils.toObject(input));
        }


    }

    public LoadedKB loadKb(String kbFile) throws FileNotFoundException {
        return new LoadedKB((KnowledgeBase<TSKRule>) yaml.load(new FileInputStream(kbFile)));
    }

    public static void main(String[] args) throws FileNotFoundException {
        new GatewayServer(new PyFrogEP()).start();
        System.out.println("Frog gateway started.");
    }
}
