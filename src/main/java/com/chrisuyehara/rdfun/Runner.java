package com.chrisuyehara.rdfun;

import com.chrisuyehara.rdfun.bean.MappedNode;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class Runner {

    public static void main(String... args) throws FileNotFoundException {
        Runner m = new Runner(args);
    }

    public Runner(String... args) throws FileNotFoundException {
        if (null == args || args.length < 2) {
            System.out.println("ERROR:  Missing required arguments, inputfile.txt outputFile.rdf");
            return;
        }

        File inputfile = new File(args[0]);
        File outputFile = new File(args[1]);

        if (false == inputfile.exists()) {
            System.out.println("File does not exist: " + inputfile.getAbsolutePath());
            return;
        }

        if (true == outputFile.exists()) {
            System.out.println("Application will not overwrite existing output file: " + outputFile.getAbsolutePath());
            return;
        }

        List<MappedNode> mappedNodes = parseInputFile(inputfile);

        buildRDF(mappedNodes);
    }

    private List<MappedNode> parseInputFile(File inputFile) throws FileNotFoundException {
        List<MappedNode> mappedNodes = new ArrayList<MappedNode>();

        FileReader reader = new FileReader(inputFile);
        Scanner scanner = new Scanner(reader);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] strings = line.split("\t");
            if ("ICD_CODE".equals(strings[0])) {
                continue;
            } else {
                MappedNode node = new MappedNode();

                node.setCode(strings[0]);
                node.setToCode(strings[7]);
                node.setToLabel(strings[8]);
                node.setLabel(strings[1]);

                mappedNodes.add(node);
            }
        }

        return mappedNodes;
    }

    private void buildRDF(List<MappedNode> mappedNodes) {
        final String CGDO_URI = "http://datasets.caregraf.org/";

        final String CGDO_NAMESPACE = "http://datasets.caregraf.org/ontology#";
        final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("cgdo", CGDO_NAMESPACE);
        model.setNsPrefix("rdfs", RDFS_NAMESPACE);

        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

        for (MappedNode node : mappedNodes) {
            int nodeIndex = 0;

            if (map.containsKey(node.getCode())) {
                nodeIndex = map.get(node.getCode());
                nodeIndex++;
            } else {
                map.put(node.getCode(), 0);
            }

            String nodeUrl = node.getCode().replace('.', '_') + "_" + nodeIndex;
            Resource root = model.createResource(CGDO_URI + "icd9cm/" + nodeUrl);

            Property icd9code = model.createProperty(CGDO_NAMESPACE + "code");
            model.add(root, icd9code, node.getCode());

            Property toLabel = model.createProperty(CGDO_NAMESPACE + "toLabel");
            model.add(root, toLabel, node.getToLabel());

            Property toCode = model.createProperty(CGDO_NAMESPACE + "toCode");
            model.add(root, toCode, node.getToCode());

            Property label = model.createProperty(RDFS_NAMESPACE + "label");
            model.add(root, label, node.getLabel());

            Property to = model.createProperty(CGDO_NAMESPACE + "to");
            Resource resource = model.createResource("http://datasets.caregraf.org/snomed/" + node.getToCode());
            model.add(root, to, resource);
        }

//        FileWriter fileWriter = new FileWriter()
        model.write(System.out);
    }
}