package com.syncfree.dbclient;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.logging.Level;

import javax.swing.UIManager;

import com.aasco.gui.LogView;
import com.aasco.util.ArgDef;
import com.aasco.util.Args;
import com.aasco.util.MultilanguageException;
import com.aasco.util.PropertyDef;
import com.aasco.util.Verbose;
import com.syncfree.dbclient.controller.IController;
import com.syncfree.dbclient.gui.MainView;
import com.syncfree.dbclient.in.CsvFileInput;
import com.syncfree.dbclient.in.IInput;

/**
 * Entry point for the application.
 * 
 * @author aas
 * @version 0.0
 */
public class DBGuiTool {
    /** The database type id. */
    private static PropertyDef<String> defaultDbId;

    // Defaults
    /** The default log level, valid values: ERROR, WARNING, INFO, DEBUG, ALL. */
    private static final String DEFAULT_LOG_LEVEL = PropertyDef.getPropertyDef(
            "com.syncfree.dbclient.log_level", Level.ALL.getName())
            .getProperty();
    /**
     * The customer properties filename. It has prevalence over the default one,
     * but it is only loaded if one has not been set in the command line.
     */
    private static final String CUSTOMER_PROPERTIES_FILENAME = "dbclient.properties";
    /** The default properties filename. */
    private static final String DEFAULT_PROPERTIES_FILENAME  = "dbclient_defaults.properties";

    private static boolean msbPropertiesLoaded = false;

    /**
     * It is the entry point for the application.
     * <p>
     * -db db_name
     * <p>
     * default db_name is "riak".
     * 
     * @param astrArgs
     *            the command line arguments.
     */
    public static final void main(final String[] astrArgs) {
        final LogView logView;

        setProperties(astrArgs);
        defaultDbId = PropertyDef.getPropertyDef(
                "com.syncfree.dbclient.db_id", "Riak");
        logView = setLogView(astrArgs);

        Verbose.debug("Entering main(String[])");

        // Set Look and Feel
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty(
                    "com.apple.mrj.application.apple.menu.about.name", "Test");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Verbose.log(e, "Failed to set look and feel; {0}", e.getMessage());
        }

        // Start GUI
        final IController controller = getController(astrArgs);

        try {
            if (controller != null) {
                final Args args = MainView.INPUT_FILE.get(astrArgs);
                IInput clientsInput = null;

                if (args != null) {
                    final String strFilename = args.getArg(0);

                    if (strFilename != null) {
                        final String[] strParts = strFilename.split("\\.");

                        if (strParts.length <= 1) {
                            Verbose.error(
                                    "Input parameter \"{0}\" without extetion",
                                    MainView.INPUT_FILE.getKey());

                            return;
                        }
                        if (strParts[strParts.length - 1].equals(CsvFileInput
                                .getType())) {
                            clientsInput = new CsvFileInput(strFilename);
                        } else {
                            Verbose.error(
                                    "Unsuported type \"{0}\" in commandline argument \"{1}\"",
                                    strParts[strParts.length - 1],
                                    MainView.INPUT_FILE.getKey());

                            return;
                        }
                    } else {
                        Verbose.error(
                                "Invalid argument with key \"{0}\"; missing first argument",
                                MainView.INPUT_FILE.getKey());

                        return;
                    }
                }
                new MainView(controller, astrArgs, logView, clientsInput);

                Verbose.info("Started!");
            }
        } catch (final Exception e) {
            Verbose.log(e, "Error detected", e.getMessage());
        } // end try

        Verbose.debug("Exiting main(String[])");
        if (Args.exists(astrArgs, "h", "?")) {
            help();
        }
    } // main()

    /**
     * Logs the simple textual help for this tool.
     */
    public static void help() {
        MainView.help();
    } // help()

    /**
     * Builds the DB controller from the provided arguments.
     * 
     * @param astrArgs
     *            the command line arguments.
     * @return the DB controller.
     */
    private static IController getController(final String[] astrArgs) {
        Args args = null;
        IController controller = null;
        String strPackage = "com.syncfree.dbclient.controller.";
        String className;
        String strDbID;

        try {
            args = MainView.KEY_DB.get(astrArgs);
        } catch (MultilanguageException e) {
            // Noting to do
        } // end try
        if (args == null) {
            strDbID = defaultDbId.getProperty();
        } else {
            strDbID = args.getValue(0, defaultDbId.getProperty());
        }
        if (Character.isUpperCase(strDbID.charAt(0))) {
            className = strDbID + "Controller";
            strDbID = strDbID.substring(0, 1).toLowerCase()
                    + strDbID.substring(1);
            strPackage += strDbID;
        } else {
            strPackage += strDbID;
            strDbID = strDbID.substring(0, 1).toUpperCase()
                    + strDbID.substring(1);
            className = strDbID + "Controller";
        }

        try {
            final Class<?> clss = Class.forName(strPackage + '.' + className);
            final Class<?>[] parameterTypes = { String[].class };
            @SuppressWarnings("unchecked")
            final Constructor<IController> c = (Constructor<IController>) clss
                    .getConstructor(parameterTypes);
            final Object[] constArgs = { astrArgs };

            controller = c.newInstance(constArgs);
            Verbose.info("Data access type \"{0}\"", controller.getID());
        } catch (final Exception cnfe) {
            Verbose.log(cnfe,
                    "Unable to obtain the DB \"{0}\" controller; {1}",
                    className, cnfe.getMessage());
        } // end try

        return controller;
    } // getController()

    /**
     * Creates a GUI log view and registers the GUI log view to be notified of
     * any logged message.
     * 
     * @param astrArgs
     *            the command line arguments.
     * @return the GUI log view.
     */
    private static LogView setLogView(final String[] astrArgs) {
        final LogView logView = new LogView();
        Args args;

        try {
            args = ArgDef.ARGDEF_LOGGING_LEVEL.get(astrArgs);
            if (args == null && DEFAULT_LOG_LEVEL != null) {
                args = ArgDef.ARGDEF_LOGGING_LEVEL.get(new String[] {
                        '-' + ArgDef.ARGDEF_LOGGING_LEVEL.getKey(),
                        DEFAULT_LOG_LEVEL });
            }
        } catch (Exception me) {
            args = null;
        } // end try
        Verbose.set(args, astrArgs, logView);

        return logView;
    } // setLogView()

    /**
     * Sets the properties filename.
     * 
     * @param astrArgs
     *            the command line arguments.
     */
    private static synchronized void setProperties(final String[] astrArgs) {
        try {
            final String strPropertiesFilename;

            if (Args.exists(astrArgs, MainView.KEY_PROPERTIES_FILENAME)) {
                final Args args = MainView.KEY_PROPERTIES_FILENAME
                        .get(astrArgs);

                strPropertiesFilename = args.getArg(0);
            } else {
                strPropertiesFilename = DEFAULT_PROPERTIES_FILENAME;
            }
            setProperties(strPropertiesFilename);
        } catch (Exception excp) {
            throw new RuntimeException(excp);
        } // end try
    } // setProperties()

    /**
     * Sets the specified properties filename.
     * 
     * @param strPropertiesFilename
     *            the properties filename.
     */
    private static synchronized void setProperties(
            final String strPropertiesFilename) {
        if (msbPropertiesLoaded) {
            Verbose.warning("Properties already loaded; ignoring request");

            return;
        }

        File oFile = new File(strPropertiesFilename);

        if (!oFile.exists()) {
            // Try to get it from jar file
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final InputStream input;

            if (classLoader == null) {
                classLoader = Class.class.getClassLoader();
            }
            input = classLoader.getResourceAsStream('/'+strPropertiesFilename);
            PropertyDef.setProperties(input, null);
        } else {
            PropertyDef.setProperties(new File(strPropertiesFilename), null, false);
        }
        if (strPropertiesFilename == DEFAULT_PROPERTIES_FILENAME) {
            oFile = new File(CUSTOMER_PROPERTIES_FILENAME);
            if (oFile.exists() && oFile.isFile()) {
                PropertyDef.addProperties(oFile);
            }
        }
        DBGuiTool.msbPropertiesLoaded = true;
    } // setProperties()
} // end class DBGuiTool
