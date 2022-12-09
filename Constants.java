public final class Constants {
    private Constants() {
    }

    // SERVER INFO
    public static final String IP = "192.168.1.189"; //Add your ip
    public static final int PORT = 2003;             // Add PORT

    // COLOR
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String PURPLE = "\u001B[35m";
    // private static final String CYAN = "\u001B[36m";
    // private static final String WHITE = "\u001B[37m";
    // private static final String BLACK = "\u001B[30m";
    
    // TEXT
    public static final String SERVER_PARAM_1 = BLUE + "<topology file>" + RESET;
    public static final String SERVER_PARAM_2 = YELLOW + "<routing-update-interval> " + RESET;
    public static final String SERVER_FAILURE = RED + "<server> missing parameter(s)." + RESET;    
    public static final String SERVER_OFFLINE = SERVER_PARAM_1 + RED + " incorrect data/data format/server offline." + RESET;
    public static final String SERVER_INVALID_FILE = SERVER_PARAM_1 + RED + " not found." + RESET;
    public static final String SERVER_INVALID_TIMEINTERVAL = SERVER_PARAM_2 + RED + "invalid." + RESET;
    public static final String INVALID_NOTIF = RED + "Invalid input." + RESET;
    public static final String HELP_NOTIFICATION = "Use 'help' command to view a list of available command(s).";
    public static final String INVALID_AND_HELP_NOTIF  = INVALID_NOTIF + " " + HELP_NOTIFICATION;
    public static final String CONNECTION_FROM = GREEN + "Connection from " + IP + ":" + PORT + RESET;
    public static final String VAGUE_OUT_OF_SERVICE = RED + "Server out of service" + RESET;
    public static final String VAUGE_ERROR = RED + "An error occurred." + RESET;
    public static final String STEP_SUCCESS = GREEN + "<step> SUCCESS" + RESET;
    public static final String STEP_FAILURE = RED + "<step> unable to send packet" + RESET;
    public static final String STEP_NO_NEIGHBORS = RED + "<step> no neigbor(s) to send packet." + RESET;
    public static final String UPDATE_SUCCESS = GREEN + "<update> SUCCESS" + RESET;
    public static final String UPDATE_FAILURE = RED + "<update> missing parameter(s)" + RESET;
    public static final String UPDATE_FAILURE_2 = RED + "<update> invalid parameter(s)" +RESET;
    public static final String DISPLAY_SUCCESS = GREEN + "\n<display> SUCCESS" + RESET;
    public static final String DISPLAY_FAILURE = RED + "<display> could not display all values." + RESET;
    public static final String DISABLE_SUCCESS = GREEN + "<disable> SUCCESS" + RESET;
    public static final String DISABLE_FAILURE_1 = RED + "<disable> server already closed." + RESET;
    public static final String DISABLE_FAILURE_2 = RED + "<disable> invalid server id." + RESET;
    public static final String DISABLE_FAILURE_3 = RED + "<disable> no server id provided." + RESET;
    public static final String DISABLE_FAILURE_4 = RED + "<disable> disabling self is prohibited." + RESET;
    public static final String DISABLE_FAILURE_5 = RED + "<disable> invalid parameter." + RESET;
    public static final String CRASH_SUCCESS = GREEN + "<crash> SUCCESS" + RESET;
    public static final String CRASH_MESSAGE = PURPLE + "FAREWELLLL..." + RESET;
    
    
    private static final String SERVER = GREEN + "server" + RESET;
    private static final String SERVER_ID = BLUE + "<server-ID>" + RESET;
    private static final String SERVER_ID_1 = BLUE + "<server-ID1>" + RESET;
    private static final String SERVER_ID_2 = YELLOW + "<server-ID2>" + RESET;
    private static final String LINK_COST = PURPLE + "<Link Cost>" + RESET;
    
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
    public static final String HELP_1 =
                  "\n Command    Description     " +
                  "\n========================================================================================================" +
                  "\n " + SERVER + " -t " + SERVER_PARAM_1 + " -i " + SERVER_PARAM_2 + 
                  "\n\n           " + SERVER_PARAM_1 + "             File contains the initial topology configuration for the server." +
                  "\n           " + SERVER_PARAM_2 + "  Time interval integer between routing updates in seconds.\n";
    public static final String HELP_2 = 
                "\n Commands \t\t\t\t\t     Description\n" +
                "==============================================================================================================="+
                "\n" + GREEN + " update " + RESET + SERVER_ID_1 + " " + SERVER_ID_2 + " " + LINK_COST + "\t     Update " + LINK_COST + " between " + SERVER_ID_1 + " and " + SERVER_ID_2 + "." +
                "\n"+ GREEN + " step                                                "+ RESET + "Send routing update to neighbors right away." + 
                "\n"+ GREEN + " packets                                             "+ RESET + "Display the number of packets the server has received." +
                "\n"+ GREEN + " display                                             "+ RESET + "Display the current routing  table." + 
                "\n"+ GREEN + " disable " + RESET + SERVER_ID + "                                 "+ "Disable Server with id " + SERVER_ID + "." +
                "\n"+ GREEN + " crash                                               "+ RESET + "Close all connections.\n" 
                ;

}