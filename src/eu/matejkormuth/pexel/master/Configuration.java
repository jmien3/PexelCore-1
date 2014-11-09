package eu.matejkormuth.pexel.master;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Class that provides configuration.
 */
@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {
    protected transient Map<String, String> data    = new HashMap<String, String>();
    protected List<ConfigurationEntry>      entries = new ArrayList<ConfigurationEntry>();
    
    private Configuration() {
        
    }
    
    /**
     * Returns values by specified key.
     * 
     * @param key
     *            key
     * @return value
     */
    public String get(final String key) {
        return this.data.get(key);
    }
    
    public String getAsString(final String key) {
        return this.data.get(key).toString();
    }
    
    public int getAsInt(final String key) {
        return Integer.parseInt(this.data.get(key));
    }
    
    /**
     * Set's value by key.
     * 
     * @param key
     *            key
     * @param value
     *            value
     */
    public void set(final String key, final String value) {
        this.data.put(key, value);
    }
    
    /**
     * Saves this configuration to specified file.
     * 
     * @param file
     *            file to save configuration.
     */
    public void save(final File file) {
        for (String key : this.data.keySet()) {
            this.entries.add(new ConfigurationEntry(key, this.data.get(key)));
        }
        try {
            JAXBContext cont = JAXBContext.newInstance(Configuration.class);
            cont.createMarshaller().marshal(this, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        this.entries.clear();
    }
    
    /**
     * Loads configuration from file.
     * 
     * @param file
     *            file
     * @return configuration
     */
    public static Configuration load(final File file) {
        Configuration conf = new Configuration();
        try {
            JAXBContext cont = JAXBContext.newInstance(Configuration.class);
            conf = (Configuration) cont.createUnmarshaller().unmarshal(file);
            return conf;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    @XmlType(name = "entry")
    protected static class ConfigurationEntry {
        public ConfigurationEntry(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
        
        @XmlAttribute(name = "key")
        public final String key;
        @XmlAttribute(name = "value")
        public final String value;
    }
    
    public static void createDefault(final File f) {
        Configuration c = new Configuration();
        c.set("authKey", "{insert 128 chars long auth key here}");
        c.set("port", "29631");
    }
}
