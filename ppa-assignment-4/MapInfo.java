import java.util.*;

/**
 * Represents the properties currently available on the map of London Boroughs based on the price
 * constraints set from the user. It possesses methods which extract, sort and search for data from
 * the available properties.
 * @Author: Tihomir Stefanov; Alexandru Bularca, Jessy Briard, Ravshanbek Rozukulov
 */

public class MapInfo {

    // Total number of Boroughs
    private static final int NUM_BOROUGHS = 33;
    // Total number of allowed indexes of information for each Borough
    private static final int INFO_INDEX = 3;
    // Then boundary for number of properties available going from the low to mid boundary
    private static final int LOW_MID_BOUNDARY = 1000;
    // Then boundary for number of properties available going from the mid to high boundary
    private static final int MID_HIGH_BOUNDARY = 2000;
    //Colour for low volume number of properties
    private static final String LOW_VOL = "-fx-background-color:#fab1a0";
    //Colour for medium volume number of properties
    private static final String MED_VOL = "-fx-background-color:#ffeaa7";
    //Colour for low high volume number of properties
    private static final String HIGH_VOL = "-fx-background-color:#55efc4";
    //Colour for absence of corresponding properties
    private static final String NO_PROPERTY = "-fx-background-color:#D0D0D0";
    //The size of the buttons in the property screen
    private static final double PREF_WIDTH = 400;

    //The boroughs of London and their corresponding coordinates in the map.
    private String[][] LondonBoroughs;
    //An array storing a list of all the properties of all homes on sale
    private ArrayList<AirbnbListing> propertyList = new ArrayList<>();
    //The abbreviations of the neighbourhoods represented on the map
    private Map<String,String> LondonAbbreviations;
    /**
     * The constructor initialises the 2d array.
     */
    public MapInfo()
    {
        LondonBoroughs = new String[NUM_BOROUGHS][INFO_INDEX];
        LondonAbbreviations = new HashMap<String,String>();
    }

    /**
     * This method returns the preferred width of the button for the list of properties
     * @return a double value with the width size of the buttons.
     */
    public double getPrefWidth()
    {
        return PREF_WIDTH;
    }

    /**
     * Return the colour with which neighbourhoods with no properties corresponding to the selected
     * price range will be represented.
     * @return a string with the colour in CSS style
     */
    public String getNoProperty() {
        return NO_PROPERTY;
    }

    /**
     * Returns the colour with which neighbourhoods with low volume of properties will be represented
     * @return a string with the colour in CSS style.
     */
    public String getLowVol()
    {
        return LOW_VOL;
    }

    /**
     * Returns the colour with which neighbourhoods with medium volume of properties will be represented
     * @return a string with the colour in CSS style.
     */
    public String getMedVol()
    {
        return MED_VOL;
    }

    /**
     * Returns the colour with which neighbourhoods with high volume of properties will be represented
     * @return a string with the colour in CSS style.
     */
    public String getHighVol()
    {
        return HIGH_VOL;
    }

    /**
     * Adds the name of the borough the x and y coordinate that it will take on the grid pane!
     * @param i The next free index in the array
     * @param name The name of the borough
     * @param x_Coordinate The x position on the grid pane
     * @param y_Coordinate The y position on the grid pane
     */
    public void addBoroughs(int i,String name, String x_Coordinate,String y_Coordinate)
    {
        LondonBoroughs[i][0] = name;
        LondonBoroughs[i][1] = x_Coordinate;
        LondonBoroughs[i][2] = y_Coordinate;
    }

    /**
     * For extendability purposes we included a method which can remove boroughs from the map
     * @param name stores the name of the borough
     */
    public void removeBoroughs(String name)
    {
        for(int i = 0; i<LondonBoroughs.length; i++) {
            if (LondonBoroughs[i][0].equals(name))
            {
                LondonBoroughs[i][0] = "";
            }

        }
    }

    /**
     * Link an abbreviation to its borough.
     * @param abbreviation The borough's abbreviation
     * @param borough The borough to link the abbreviation to
     */
    public void addAbbreviations(String abbreviation, String borough)
    {
        LondonAbbreviations.put(abbreviation,borough);
    }

    /**
     * This method returns the neighbourhood's name from a specified abbreviation.
     * @param abbreviation The borough's abbreviation
     * @return The corresponding borough's name
     */
    public String getNeighbourhood(String abbreviation)
    {
        return LondonAbbreviations.get(abbreviation);
    }

    /**
     * This method returns the abbreviation linked to the specified borough
     * @param borough The borough to get the abbreviation from
     * @return The borough's abbreviation
     */
    public String getAbbreviation(String borough)
    {
        String key = null;
        for(String keys:LondonAbbreviations.keySet())
        {
            if(LondonAbbreviations.get(keys).equals(borough))
            {
                key = keys;
            }
        }
        return key;
    }
    /**
     * For extensibility purposes we included a accessor method which can access the total number
     * of neighbourhoods on the map.
     * @return an integer which show the total number of neighbourhoods on the map
     */
    public int returnTotalNumNeighbourhoods()
    {
        return LondonBoroughs.length;
    }

    /**
     * Returns the 2d array which stores the London Boroughs
     * @return a 2d array of String type
     */
    public String[][] getLondonBoroughs()
    {
        return LondonBoroughs;
    }

    /**
     * This method returns a color scheme for the map of the boroughs, corresponding to the number of properties
     * in the borough in the selected price range.
     * @param neighbourhood a string variable which stores the name of the neighbourhood
     * @return the hexadecimal representation of the colour to show on the map for that neighbourhood
     */
    public String propertyVolumeColour(String neighbourhood)
    {

        int counter = getNumberOfOccurrences(neighbourhood);
        return compareVolume(counter);
    }

    /**
     * This method counts the total number of properties that are currently on sale in a specified neighbourhood.
     * @return The number of occurrences of a neighbourhood in the list of properties corresponding to the selected price range
     */
    public int getNumberOfOccurrences(String neighbourhood)
    {
        int counter = 0;
        for (AirbnbListing ar:propertyList)
        {
            if (neighbourhood.equals(ar.getNeighbourhood()))
            {
                counter+=1;
            }
        }
        return counter;
    }

    /**
     * Updates the list of properties which can be used to generate the map based on price
     * @param properties it uses an array list which stores all the information about each property
     */
    public void setPropertyData(ArrayList<AirbnbListing> properties)
    {
        propertyList = properties;
    }

    /**
     * Returns the list of properties available based on neighbourhood
     * @param neighbourhood name of the neighbourhood as a String
     * @return an array with all the information about every property available
     */
    public ArrayList<AirbnbListing> getPropertyList(String neighbourhood)
    {
        ArrayList<AirbnbListing> filteredProperties = new ArrayList<>();
        for (AirbnbListing ar:propertyList) {
            if (neighbourhood.equals(ar.getNeighbourhood())) {
                filteredProperties.add(ar);
            }
        }
        return filteredProperties;
    }

    /**
     * It takes the counted number of properties for that neighbourhood and decides which colour of
     * availability should give so that it is represented in the map.
     * @param counter used to store the number of properties available for sale in that neighbourhood
     * @return a string value which stores a static variable consisting of the hexadecimal representation
     * of the colour to show on the map
     */
    private String compareVolume(int counter)
    {
        if (counter == 0) {
            return NO_PROPERTY;
        }
        else if (counter>=0 && counter<LOW_MID_BOUNDARY)
        {
            return LOW_VOL;
        }
        else if (counter>=LOW_MID_BOUNDARY && counter<MID_HIGH_BOUNDARY)
        {
            return MED_VOL;
        }
        else
        {
            return HIGH_VOL;
        }
    }

    /**
     * This method sorts all the properties within a given range by their number of reviews
     * It uses a comparator and a collections class as well as a sort method which is able to return the
     * ordered version of the arrayList in descending order.
     * Inspiration taken from: link: https://youtube.be/wzWFQTLn8hl
     */
    public void sortPropertyByNumReviews()
    {
        Collections.sort(propertyList, new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2)
            {
                return Integer.valueOf(property2.getNumberOfReviews()).compareTo(property1.getNumberOfReviews());
            }
        });
    }

    /**
     * This method sorts all the properties within a given range by their price from cheapest to most
     * expensive. It uses a comparator and a collections class as well as a sort
     * method which is able to return the ordered version of the arrayList in descending order.
     * Inspiration taken from: link: https://youtube.be/wzWFQTLn8hl
     */
    public void sortPropertyByPrice()
    {
        Collections.sort(propertyList, new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2)
            {
                return Integer.valueOf(property1.getPrice()).compareTo(property2.getPrice());
            }
        });
    }

    /**
     * This method sorts all the properties within a given range by their host name in alphabetical order
     * It uses a comparator and a collections class as well as a sort method which is able to return the
     * ordered version of the arrayList in descending order.
     * Inspiration taken from: link: https://youtube.be/wzWFQTLn8hl
     */
    public void sortPropertyByHostName()
    {
        Collections.sort(propertyList, new Comparator<AirbnbListing>() {
            public int compare(AirbnbListing property1, AirbnbListing property2)
            {
                return String.valueOf(property1.getHost_name()).compareTo(property2.getHost_name());
            }
        });
    }

    /**
     * This method searches for the propery that has been selected from the user and
     * produces a description of the property with all the details that haven't been
     * already revealed.
     * @param propertyID holds the id of the property from which the property can be identified
     * @return return a String which stores the description of the property.
     */
    public String showPropertyDescription(String propertyID)
    {
        String description;
        for (AirbnbListing property: propertyList)
        {
            if(property.getId().equals(propertyID))
            {
                description = "Host name: "+property.getHost_name()
                        +"\nProperty name: "+property.getName()
                        +"\nBorough: "+property.getNeighbourhood()
                        +"\nMinimum nights: "+property.getMinimumNights()
                        +"\nPropertyID: "+property.getId()
                        +"\nLast Review: "+property.getLastReview()
                        +"\nRoom type: "+property.getRoom_type()
                        +"\nAvailability throughout the year: "+property.getAvailability365()
                        +"\nHost listings account: "+property.getCalculatedHostListingsCount()
                        +"\nProperty latitude: "+property.getLatitude()
                        +"\nProperty longitude: "+property.getLongitude()
                        +"\nAverage number of reviews per month: "+property.getReviewsPerMonth();
                return description;
            }
        }
        description = "No description available for that property!";
        return description;
    }

    /**
     *
     * @param intString the string to be converted to Integer type
     * @return the Integer value of the string, or -1 of the string is
     * either empty or just whitespace
     */
    public Integer convertInt(String intString){
        if(intString != null && !intString.trim().equals("")){
            return Integer.parseInt(intString);
        }
        return -1;
    }
}

