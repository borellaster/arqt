//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v3.0.0 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2025.09.09 às 02:28:53 PM BRT 
//


package br.com.borella.acl.wsdl.expenses;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.com.borella.acl.wsdl.expenses package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.com.borella.acl.wsdl.expenses
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetExpensesByDateRequest }
     * 
     */
    public GetExpensesByDateRequest createGetExpensesByDateRequest() {
        return new GetExpensesByDateRequest();
    }

    /**
     * Create an instance of {@link GetExpensesByDateResponse }
     * 
     */
    public GetExpensesByDateResponse createGetExpensesByDateResponse() {
        return new GetExpensesByDateResponse();
    }

    /**
     * Create an instance of {@link Expense }
     * 
     */
    public Expense createExpense() {
        return new Expense();
    }

}
