import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class Mainjob {

    static int indexSite;
    static List<Site> sites;
    static List<Variable> variables;
    static List<Domaine> domains;
    static List<Contrainte> contraintes ;
    public static void main(String argv[]) throws IOException {
        indexSite = 0;
        sites = new ArrayList<Site>();
        variables = new ArrayList<Variable>();
        domains = new ArrayList<Domaine>();
        contraintes = new ArrayList<Contrainte>();
        getVariables();
        getSites();
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootelement = doc.createElement("instance");
            doc.appendChild(rootelement);
            Element presentation = doc.createElement("presentation");
            rootelement.appendChild(presentation);
            Attr attr = doc.createAttribute("name");
            Attr attr1 = doc.createAttribute("maxConstraintArity");
            Attr attr2= doc.createAttribute("maximize");
            Attr attr3= doc.createAttribute("format");
            attr.setValue("sampleProblem");
            attr1.setValue("2");
            attr2.setValue("false");
            attr3.setValue("XCSP 2.1_FRODO");
            presentation.setAttributeNode(attr);
            presentation.setAttributeNode(attr1);
            presentation.setAttributeNode(attr2);
            presentation.setAttributeNode(attr3);
            Element agents = doc.createElement("agents");
            rootelement.appendChild(agents);
            Attr attragent = doc.createAttribute("nbAgents");
            attragent.setValue(String.valueOf(sites.size()));
            agents.setAttributeNode(attragent);
            for(Site site : sites) {
            Element agent = doc.createElement("agent");
            Attr x = doc.createAttribute("name");
            x.setValue(String.valueOf(site.numero));
            agent.setAttributeNode(x);
            agents.appendChild(agent);
            }


            BufferedReader br;
            br = new BufferedReader(
                    new FileReader("C:\\Users\\Ahmed\\Desktop\\CELAR\\scen01\\DOM.TXT"));
            String line = br.readLine();

            while (line != null) {
                Domaine domaine1 = new Domaine();
                StringTokenizer st1 = new StringTokenizer(line);
                domaine1.domain=st1.nextToken();
               // System.out.println(domaine1.domain);
                domaine1.nbrVal=st1.nextToken();
                //System.out.println(domaine1.nbrVal)
              domaine1.vals = line.substring(7);
             // System.out.println(domaine1.vals);
                domains.add(domaine1);
                line = br.readLine();
            }

            System.out.println(domains.size());


            br.close();

            Element domains1 = doc.createElement("domains");
            rootelement.appendChild(domains1);
            Attr nbdomain = doc.createAttribute("nbDomains");
            nbdomain.setValue(String.valueOf(domains.size()));
            domains1.setAttributeNode(nbdomain);
           for(Domaine domaine : domains){
                Element domain = doc.createElement("domain");
                Attr name = doc.createAttribute("name");
                Attr nbValues = doc.createAttribute("nbValues");
                name.setValue(domaine.domain);
                nbValues.setValue(domaine.nbrVal);
                domain.setAttributeNode(name);
                domain.setAttributeNode(nbValues);
                domain.appendChild(doc.createTextNode(domaine.vals));
                domains1.appendChild(domain);

            }


            Element variables1 = doc.createElement("variables");
            rootelement.appendChild(variables1);
            Attr nbVariables = doc.createAttribute("nbVariables");
            nbVariables.setValue(String.valueOf(variables.size()));
            variables1.setAttributeNode(nbVariables);
            for( Variable var : variables){
                Element x = doc.createElement("variable");
                Attr name = doc.createAttribute("name");
                Attr domain = doc.createAttribute("domain");
                Attr agent = doc.createAttribute("agent");
                name.setValue(var.var);
                domain.setValue(var.domaine);
                agent.setValue(var.site);
                x.setAttributeNode(name);
                x.setAttributeNode(domain);
                x.setAttributeNode(agent);
                variables1.appendChild(x);
            }


            BufferedReader br1;
            br1 = new BufferedReader(
                    new FileReader("C:\\Users\\Ahmed\\Desktop\\CELAR\\scen01\\CTR.TXT"));
            String line1 = br1.readLine();

            while (line1 != null) {
                Contrainte contrainte = new Contrainte();
                StringTokenizer st1 = new StringTokenizer(line1);
                contrainte.var1=st1.nextToken();
                contrainte.var2=st1.nextToken();
                contrainte.nom = ""+contrainte.var1+"_"+contrainte.var2+"_consitraint";
                contrainte.type=st1.nextToken();
                contrainte.operator = st1.nextToken();
                contrainte.value = st1.nextToken();
                contraintes.add(contrainte);
                line1 = br1.readLine();
            }

            System.out.println(contraintes.size());


            br1.close();



            Element constraints = doc.createElement("constraints");
            rootelement.appendChild(constraints);
            Attr nbConstraints = doc.createAttribute("nbConstraints");
            nbConstraints.setValue(String.valueOf(contraintes.size()));
            constraints.setAttributeNode(nbConstraints);
            for(Contrainte contrainte : contraintes)
            {
                Element constraint = doc.createElement("constraint");
                Attr name = doc.createAttribute("name");
                Attr arity = doc.createAttribute("arity");
                Attr scope = doc.createAttribute("scope");
                Attr ref = doc.createAttribute("reference");
                Element para = doc.createElement("parameters");
                name.setValue(contrainte.nom);
                arity.setValue("2");
                scope.setValue(contrainte.var1.concat(" ").concat(contrainte.var2));
                if(contrainte.operator=="="){ref.setValue("MYFF");}
                else{ref.setValue("MYF");}
                para.appendChild(doc.createTextNode(contrainte.var1.concat(" ").concat(contrainte.var2).concat(" ").concat(contrainte.value)));
                constraint.setAttributeNode(name);
                constraint.setAttributeNode(arity);
                constraint.setAttributeNode(scope);
                constraint.setAttributeNode(ref);
                constraint.appendChild(para);
                constraints.appendChild(constraint);


            }



            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\Users\\Ahmed\\Desktop\\file.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");







        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    private static void getVariables() throws IOException {
        BufferedReader br;
        br = new BufferedReader(
                new FileReader("C:\\Users\\Ahmed\\Desktop\\CELAR\\scen01\\VAR.TXT"));
        String line = br.readLine();
        while (line != null) {
            Variable var = new Variable();
            var.var = line.substring(0, 3);
            var.domaine = line.substring(6, 7);
            variables.add(var);
            line = br.readLine();
        }
        br.close();

    }



    private static void getSites() throws IOException {

        BufferedReader br;
        br = new BufferedReader(
                new FileReader("C:\\Users\\Ahmed\\Desktop\\CELAR\\scen01\\CTR.TXT"));
        String line = br.readLine();
        while (line != null) {
            String var1 = line.substring(0, 3);
            String var2 = line.substring(4, 7);
            String typeCon = line.substring(8, 9);
            if (typeCon.equals("C")) {
                if (addSite(var1, var2) == 0) {
                    if (addSite(var2, var1) == 0) {
                        indexSite++;
                        Site site = new Site();
                        site.numero = indexSite;
                        site.variables = new ArrayList<String>();
                        site.variables.add(var1);
                        site.variables.add(var2);
                        setSite(site.numero, var1);
                        setSite(site.numero, var2);
                        sites.add(site);
                    }
                }
            }
            // System.err.println("|" + line.substring(0, 3) + "|" + line.substring(4, 7) +
            // "|" + line.substring(8, 9)
            // + "|" + line.substring(10, 11) + "|" + line.substring(12, 15) + "|");
            line = br.readLine();
        }
        br.close();
    }

    private static int addSite(String var1, String var2) {
        // System.err.println("sites size= " + sites.size());
        for (Site site : sites) {
            for (String var : site.variables) {
                // System.err.println("var1= " + var1 + " var= " + var);
                if (var.equals(var1)) {
                    // System.err.println("cond");
                    site.variables.add(var2);
                    setSite(site.numero, var2);
                    return site.numero;
                }
            }
        }
        return 0;
    }

    private static void setSite(int site, String var) {
        boolean nontrouve = true;
        int i = 0;
        while (nontrouve) {
            if (variables.get(i).var.equals(var)) {
                variables.get(i).site = site + "";
                nontrouve = false;
            }
            i++;
        }

    }
}