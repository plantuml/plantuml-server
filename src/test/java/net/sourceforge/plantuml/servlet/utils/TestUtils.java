package net.sourceforge.plantuml.servlet.utils;


/**
 * Utility class for the unit tests
 */
public abstract class TestUtils {

    //
    // Theses strings are the compressed form of a PlantUML diagram.
    //

    /**
     * version
     */
    public static final String VERSION = "AqijAixCpmC0";

    /**
     * version
     */
    public static final String VERSIONCODE = "@startuml\nversion\n@enduml";

    /**
     * Bob -> Alice : hello
     */
    public static final String SEQBOB = "SyfFKj2rKt3CoKnELR1Io4ZDoSa70000";

    /**
     * Bob -> Alice : hello
     */
    public static final String SEQBOBCODE = "@startuml\nBob -> Alice : hello\n@enduml";

    /**
     * Encoded/compressed diagram source to text multipage uml diagrams.
     *
     * Bob -> Alice : hello
     * newpage
     * Bob <- Alice : hello
     * Bob -> Alice : let's talk [[tel:0123456789]]
     * Bob <- Alice : better not
     * Bob -> Alice : <&rain> bye
     * newpage
     * Bob <- Alice : bye
     */
    public static final String SEQMULTIPAGE = "SoWkIImgAStDuNBAJrBGjLDmpCbCJbMmKiX8pSd9vyfBBIz8J4y5IzheeagYwyX9BL4lLYX9pCbMY8ukISsnCZ0qCZOnDJEti8oDHJSXARMa9BL88I-_1DqO6xMYnCmyEuMaobGSreEb75BpKe3E1W00";

}
