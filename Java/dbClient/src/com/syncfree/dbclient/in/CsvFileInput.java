package com.syncfree.dbclient.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.syncfree.dbclient.data.IClient;

/**
 * Implementation to read a text file.
 * <p>
 * client_name,connection_type,url/host,[controls,][port]
 * <p>
 * Any line that start with the comment ("#") string is ignored.
 * 
 * @author aas
 * @version 0.0
 */
public class CsvFileInput implements IInput {
    /** Starting string that identify a comment line */
    private static final String COMMENT = "#";

    // Index of the fields in each row in the CSV
    protected static final int INDEX_CLIENT = 0;
    protected static final int INDEX_TYPE = 1;
    protected static final int INDEX_CONNECTION = 2;
    protected static final int INDEX_CONTROLS = 3;
    protected static final int INDEX_PORT = 4;
    
    private static final int INVALID_PORT = -1;

    /** Provides access to the input file. */
    private BufferedReader mIn;
    /** Holds the last line read from the associated file. */
    private String mstrLine;

    /**
     * Builds the CSV file reader for client's details.
     * 
     * @param strFilename
     *            the name of the file.
     * @throws FileNotFoundException
     *             when the file does not exist.
     */
    public CsvFileInput(final String strFilename) throws FileNotFoundException {
        this(new File(strFilename));
    } // Constructor ()

    /**
     * Builds the CSV file reader for client's details.
     * 
     * @param file
     *            the CSV file to read from.
     * @throws FileNotFoundException
     *             when the file does not exist.
     */
    public CsvFileInput(final File file) throws FileNotFoundException {
        this(new FileReader(file));
    } // Constructor ()

    /**
     * Builds the CSV file reader for client's details.
     * 
     * @param reader
     *            the access to the CSV content.
     * @throws FileNotFoundException
     *             when the file does not exist.
     */
    public CsvFileInput(final Reader reader) {
        this.mIn = new BufferedReader(reader);
    } // Constructor ()

    /**
     * @return the extension.
     */
    public static String getType() {
        return "csv";
    } // getType()

    @Override
    public boolean hasNext() throws IOException {
        if (this.mstrLine == null) {
            do {
                this.mstrLine = null; // make sure it is null before getting any
                                        // new line
                this.mstrLine = this.mIn.readLine();
            } while (this.mstrLine != null && (this.mstrLine.length() <= 10
                    || this.mstrLine.indexOf(COMMENT) == 0));
        }

        return (this.mstrLine != null);
    } // hasNext()

    @Override
    public IClient next() throws FileNotFoundException {
        final String[] astrFields = this.mstrLine.split(",");
        final IClient client = new SimpleIClient(astrFields);

        this.mstrLine = null;

        return client;
    } // next()

    /**
     * Representation of a the client's details.
     * 
     * @author aas
     * @version 0.0
     */
    public class SimpleIClient implements IClient {
        private final String mstrName;
        private final TYPE mType;
        private final String mstrConnection;
        private final int miPort;

        /**
         * Builds the client's details from the provided data.
         * 
         * @param astrFields
         *            the client's details.
         */
        SimpleIClient(final String[] astrFields) {
            this.mstrName = astrFields[INDEX_CLIENT];
            this.mType = TYPE.valueOf(astrFields[INDEX_TYPE].toUpperCase());
            this.mstrConnection = astrFields[INDEX_CONNECTION];
            if (this.mType == TYPE.PBC) {
                this.miPort = Integer.parseInt(astrFields[INDEX_PORT]);
            } else {
                this.miPort = INVALID_PORT;
            }
        } // Constructor ()

        @Override
        public String getClientName() {
            return this.mstrName;
        } // getClientName()

        @Override
        public TYPE getClientType() {
            return this.mType;
        } // getClientType()

        @Override
        public String getURL() {
            return this.mstrConnection;
        } // getURL()

        @Override
        public int getPort() {
            return this.miPort;
        } // getPort()

        @Override
        public String getHost() {
            return this.mstrConnection;
        } // getHost()

        @Override
        public boolean isConnected() {
            return false;
        } // isConnected()

        @Override
        public String toString() {
            return this.mstrName;
        } // toString()
    } // end class SimpleIClient
} // end class CsvFileInput
