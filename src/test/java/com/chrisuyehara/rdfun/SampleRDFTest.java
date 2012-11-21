package com.chrisuyehara.rdfun;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.junit.Test;

public class SampleRDFTest {

    @Test
    public void testJenaOutput() throws Exception {
        final String CGDO_URI = "http://datasets.caregraf.org/";

        final String CGDO_NAMESPACE = "http://datasets.caregraf.org/ontology#";
        final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";

        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("cgdo", CGDO_NAMESPACE);
        model.setNsPrefix("rdfs", RDFS_NAMESPACE);

        Resource root = model.createResource(CGDO_URI + "icd9cm/200_1_2");

        Property icd9code = model.createProperty(CGDO_NAMESPACE + "code");
        model.add(root, icd9code, "277.39");

        Property toLabel = model.createProperty(CGDO_NAMESPACE + "toLabel");
        model.add(root, toLabel, "insert label here");

        Property toCode = model.createProperty(CGDO_NAMESPACE + "toCode");
        model.add(root, toCode, "insert code");

        Property label = model.createProperty(RDFS_NAMESPACE + "label");
        model.add(root, label, "RDFS label");

        Property to = model.createProperty(CGDO_NAMESPACE + "to");
        Resource resource = model.createResource("http://datasets.caregraf.org/snomed/402457007");
        model.add(root, to, resource);

        model.write(System.out);
    }

}
