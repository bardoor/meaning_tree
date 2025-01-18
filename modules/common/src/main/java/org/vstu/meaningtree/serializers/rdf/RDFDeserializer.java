package org.vstu.meaningtree.serializers.rdf;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.serializers.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RDFDeserializer implements Deserializer<Model> {
    public static final String NS = "http://vstu.ru/poas/code#";
    public static final String NS_base = "http://vstu.ru/poas/code";

    public AbstractSerializedNode deserialize(Model model, Resource rootResource) {
        return deserializeNode(model, rootResource);
    }

    private Object recognizeLiteral(Literal literal) {
        if (literal.getDatatype().equals(XSDDatatype.XSDstring)) {
            return literal.getString();
        } else if (literal.getDatatype().equals(XSDDatatype.XSDinteger) || literal.getDatatype().equals(XSDDatatype.XSDint)) {
            return literal.getInt();
        } else if (literal.getDatatype().equals(XSDDatatype.XSDlong)) {
            return literal.getLong();
        } else if (literal.getDatatype().equals(XSDDatatype.XSDboolean)) {
            return literal.getBoolean();
        } else if (literal.getDatatype().equals(XSDDatatype.XSDdouble)) {
            return literal.getDouble();
        } else if (literal.getDatatype().equals(XSDDatatype.XSDfloat)) {
            return literal.getFloat();
        }
        return literal.getString();
    }

    private AbstractSerializedNode deserializeNode(Model model, Resource resource) {
        boolean isListNode = resource.hasProperty(RDF.type, model.createResource(NS + "ListNode"));

        if (isListNode) {
            List<SerializedNode> nodes = new ArrayList<>();
            RDFList list = resource.getPropertyResourceValue(model.createProperty(NS, "hasFieldList")).as(RDFList.class);
            list.iterator().forEachRemaining(item -> nodes.add((SerializedNode) deserializeNode(model, item.asResource())));
            return new SerializedListNode(nodes);
        } else {
            String nodeName = resource.getPropertyResourceValue(model.createProperty(NS, "nodeType")).getLocalName();
            Map<String, AbstractSerializedNode> fields = new HashMap<>();
            Map<String, Object> values = new HashMap<>();

            StmtIterator properties = resource.listProperties();
            while (properties.hasNext()) {
                Statement stmt = properties.nextStatement();

                if (stmt.getObject().isLiteral()) {
                    values.put(stmt.getPredicate().getLocalName(), recognizeLiteral(stmt.getLiteral()));
                } else if (stmt.getObject().isResource() && stmt.getPredicate().getLocalName().equals("hasField")) {
                    Resource fieldResource = stmt.getObject().asResource();
                    fields.put(
                            fieldResource.getPropertyResourceValue(model.createProperty(NS, "fieldName")).getLocalName(),
                            deserializeNode(model, fieldResource));
                }
            }
            return new SerializedNode(nodeName, fields, values);
        }
    }

    @Override
    public Node deserialize(Model serialized) {
        return new UniversalDeserializer().deserialize(deserialize(serialized,
                serialized.getResource(NS + "MeaningTree").getPropertyResourceValue(serialized.createProperty(NS, "hasField")))
        );
    }
}