public final class Constants {
    private Constants() {
    }

    // SERVER INFO
    public static final String IP = ""; //Add your ip
    public static final int PORT = 2000;

    // COLOR
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    // private static final String YELLOW = "\u001B[33m";
    // private static final String PURPLE = "\u001B[35m";
    // private static final String CYAN = "\u001B[36m";
    // private static final String WHITE = "\u001B[37m";
    // private static final String BLACK = "\u001B[30m";
    
    // TEXT
    public static final String SERVER_PARAM_1 = GREEN + "<topology file>" + RESET;
    public static final String SERVER_PARAM_2 = BLUE + "<routing-update-interval> " + RESET;
    public static final String INVALID_NOTIF = RED + "Invalid input." + RESET;
    public static final String HELP_NOTIFICATION = "Use 'help' command to view a list of available command(s).";
    public static final String INVALID_AND_HELP_NOTIF  = INVALID_NOTIF + " " + HELP_NOTIFICATION;
    public static final String CONNECTION_FROM = GREEN + "Connection from " + IP + ":" + PORT;
    public static final String VAGUE_OUT_OF_SERVICE = "Server out of service";
    public static final String VAUGE_ERROR = "An error occurred.";
    
    // SERVER RESPONSES

    //STRING METHODS
    public static final String connectedTo(String ip, String port) {
        return GREEN + "Connected to " + ip + ":" + port + RESET;
    }

    // DISPLAYS
    private static final String SERVER_TITLE = """
             ===========================================================
             |                                                         |
             |    SSSSSS  EEEEEE  RRRRR   V       V EEEEEE  RRRRR      |
             |    S       E       R    R   V     V  E       R    R     |
             |    SSSSSS  EEEE    RRRRR     V   V   EEEE    RRRRR      |
             |         S  E       R    R     V V    E       R    R     |
             |    SSSSSS  EEEEEE  R     R     V     EEEEEE  R     R    |
             |                                                         |
             |                                              Port:""" + PORT + """
               |
             ===========================================================\n""";

    public static final String INTRO_MSG = "\t\t\tWELCOME TO\n" + SERVER_TITLE + "  " + HELP_NOTIFICATION;
    public static final String HELP_1 = """
        \tserver -t""" + " " + SERVER_PARAM_1 + " " + """
                        -i """ + " " + SERVER_PARAM_2 + "\n                 " + SERVER_PARAM_1 + """
                                                The topology file contains the initial topology configuration for the server, e.g., timberlake_init.txt.
                                """ + "                 " + SERVER_PARAM_2 + """
                                             It specifies the time interval between routing updates in seconds.
                                        """;
    public static final String HELP_2 = """
        \tupdate    .....
                step      .....
                packets   .....
                display   .....
                crash     .....
                             """;

}