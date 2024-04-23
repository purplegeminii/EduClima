import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;

/**
 * This class provides a list of countries and their
 * two-letter codes for use in other classes.
 *
 * @author Delali Nsiah-Asare
 * @author Obed Babington
 * @author Ewurama Boateng
 */
public class CountryInfo {
    // ObservableList of countries to be added to Main's drop-down country menu
    public static ObservableList<String> countries;
    // HashMap of Countries w/ their Respective Two-Letter Codes
    public static HashMap<String, String> countryCodes;


    /**
     * Populate the countries ObservableList
     */
    public static void populateCountries(){
        countries = FXCollections.observableArrayList();
        countries.addAll(
                "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda", "Argentina",
                "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados",
                "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana",
                "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",
                "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Costa Rica", "Croatia",
                "Cuba", "Cyprus", "Czech Republic", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica",
                "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
                "Estonia", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana",
                "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary",
                "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Ivory Coast", "Jamaica",
                "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon",
                "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi", "Malaysia",
                "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova",
                "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
                "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Korea", "North Macedonia",
                "Norway", "Oman", "Pakistan", "Palau", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru",
                "Philippines", "Poland", "Portugal", "Qatar", "Republic of the Congo", "Romania", "Russia", "Rwanda",
                "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino",
                "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore",
                "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea", "South Sudan",
                "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan",
                "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
                "Turkmenistan", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom",
                "United States of America", "Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
                "Yemen", "Zambia","Zimbabwe"
                );
    }

    /**
     * Populate the countryCodes HashMap
     */
    public static void populateCountryCodes(){
        countryCodes = new HashMap<>();
        countryCodes.put("Afghanistan", "AF");
        countryCodes.put("Albania", "AL");
        countryCodes.put("Algeria", "DZ");
        countryCodes.put("Andorra", "AD");
        countryCodes.put("Angola", "AO");
        countryCodes.put("Antigua and Barbuda", "AG");
        countryCodes.put("Argentina", "AR");
        countryCodes.put("Armenia", "AM");
        countryCodes.put("Australia", "AU");
        countryCodes.put("Austria", "AT");
        countryCodes.put("Azerbaijan", "AZ");
        countryCodes.put("Bahamas", "BS");
        countryCodes.put("Bahrain", "BH");
        countryCodes.put("Bangladesh", "BD");
        countryCodes.put("Barbados", "BB");
        countryCodes.put("Belarus", "BY");
        countryCodes.put("Belgium", "BE");
        countryCodes.put("Belize", "BZ");
        countryCodes.put("Benin", "BJ");
        countryCodes.put("Bhutan", "BT");
        countryCodes.put("Bolivia", "BO");
        countryCodes.put("Bosnia and Herzegovina", "BA");
        countryCodes.put("Botswana", "BW");
        countryCodes.put("Brazil", "BR");
        countryCodes.put("Brunei", "BN");
        countryCodes.put("Bulgaria", "BG");
        countryCodes.put("Burkina Faso", "BF");
        countryCodes.put("Burundi", "BI");
        countryCodes.put("Cambodia", "KH");
        countryCodes.put("Cameroon", "CM");
        countryCodes.put("Canada", "CA");
        countryCodes.put("Cape Verde", "CV");
        countryCodes.put("Central African Republic", "CF");
        countryCodes.put("Chad", "TD");
        countryCodes.put("Chile", "CL");
        countryCodes.put("China", "CN");
        countryCodes.put("Colombia", "CO");
        countryCodes.put("Comoros", "KM");
        countryCodes.put("Costa Rica", "CR");
        countryCodes.put("Croatia", "HR");
        countryCodes.put("Cuba", "CU");
        countryCodes.put("Cyprus", "CY");
        countryCodes.put("Czech Republic", "CZ");
        countryCodes.put("Democratic Republic of the Congo", "CD");
        countryCodes.put("Denmark", "DK");
        countryCodes.put("Djibouti", "DJ");
        countryCodes.put("Dominica", "DM");
        countryCodes.put("Dominican Republic", "DO");
        countryCodes.put("East Timor", "TL");
        countryCodes.put("Ecuador", "EC");
        countryCodes.put("Egypt", "EG");
        countryCodes.put("El Salvador", "SV");
        countryCodes.put("Equatorial Guinea", "GQ");
        countryCodes.put("Eritrea", "ER");
        countryCodes.put("Estonia", "EE");
        countryCodes.put("Ethiopia", "ET");
        countryCodes.put("Fiji", "FJ");
        countryCodes.put("Finland", "FI");
        countryCodes.put("France", "FR");
        countryCodes.put("Gabon", "GA");
        countryCodes.put("Gambia", "GM");
        countryCodes.put("Georgia", "GE");
        countryCodes.put("Germany", "DE");
        countryCodes.put("Ghana", "GH");
        countryCodes.put("Greece", "GR");
        countryCodes.put("Grenada", "GD");
        countryCodes.put("Guatemala", "GT");
        countryCodes.put("Guinea", "GN");
        countryCodes.put("Guinea-Bissau", "GW");
        countryCodes.put("Guyana", "GY");
        countryCodes.put("Haiti", "HT");
        countryCodes.put("Honduras", "HN");
        countryCodes.put("Hungary", "HU");
        countryCodes.put("Iceland", "IS");
        countryCodes.put("India", "IN");
        countryCodes.put("Indonesia", "ID");
        countryCodes.put("Iran", "IR");
        countryCodes.put("Iraq", "IQ");
        countryCodes.put("Ireland", "IE");
        countryCodes.put("Israel", "IL");
        countryCodes.put("Italy", "IT");
        countryCodes.put("Ivory Coast", "CI");
        countryCodes.put("Jamaica", "JM");
        countryCodes.put("Japan", "JP");
        countryCodes.put("Jordan", "JO");
        countryCodes.put("Kazakhstan", "KZ");
        countryCodes.put("Kenya", "KE");
        countryCodes.put("Kiribati", "KI");
        countryCodes.put("Kuwait", "KW");
        countryCodes.put("Kyrgyzstan", "KG");
        countryCodes.put("Laos", "LA");
        countryCodes.put("Latvia", "LV");
        countryCodes.put("Lebanon", "LB");
        countryCodes.put("Lesotho", "LS");
        countryCodes.put("Liberia", "LR");
        countryCodes.put("Libya", "LY");
        countryCodes.put("Liechtenstein", "LI");
        countryCodes.put("Lithuania", "LT");
        countryCodes.put("Luxembourg", "LU");
        countryCodes.put("Madagascar", "MG");
        countryCodes.put("Malawi", "MW");
        countryCodes.put("Malaysia", "MY");
        countryCodes.put("Maldives", "MV");
        countryCodes.put("Mali", "ML");
        countryCodes.put("Malta", "MT");
        countryCodes.put("Marshall Islands", "MH");
        countryCodes.put("Mauritania", "MR");
        countryCodes.put("Mauritius", "MU");
        countryCodes.put("Mexico", "MX");
        countryCodes.put("Micronesia", "FM");
        countryCodes.put("Moldova", "MD");
        countryCodes.put("Monaco", "MC");
        countryCodes.put("Mongolia", "MN");
        countryCodes.put("Montenegro", "ME");
        countryCodes.put("Morocco", "MA");
        countryCodes.put("Mozambique", "MZ");
        countryCodes.put("Myanmar", "MM");
        countryCodes.put("Namibia", "NA");
        countryCodes.put("Nauru", "NR");
        countryCodes.put("Nepal", "NP");
        countryCodes.put("Netherlands", "NL");
        countryCodes.put("New Zealand", "NZ");
        countryCodes.put("Nicaragua", "NI");
        countryCodes.put("Niger", "NE");
        countryCodes.put("Nigeria", "NG");
        countryCodes.put("North Korea", "KP");
        countryCodes.put("North Macedonia", "MK");
        countryCodes.put("Norway", "NO");
        countryCodes.put("Oman", "OM");
        countryCodes.put("Pakistan", "PK");
        countryCodes.put("Palau", "PW");
        countryCodes.put("Palestine", "PS");
        countryCodes.put("Panama", "PA");
        countryCodes.put("Papua New Guinea", "PG");
        countryCodes.put("Paraguay", "PY");
        countryCodes.put("Peru", "PE");
        countryCodes.put("Philippines", "PH");
        countryCodes.put("Poland", "PL");
        countryCodes.put("Portugal", "PT");
        countryCodes.put("Qatar", "QA");
        countryCodes.put("Romania", "RO");
        countryCodes.put("Russia", "RU");
        countryCodes.put("Rwanda", "RW");
        countryCodes.put("Saint Kitts and Nevis", "KN");
        countryCodes.put("Saint Lucia", "LC");
        countryCodes.put("Saint Vincent and the Grenadines", "VC");
        countryCodes.put("Samoa", "WS");
        countryCodes.put("San Marino", "SM");
        countryCodes.put("Sao Tome and Principe", "ST");
        countryCodes.put("Saudi Arabia", "SA");
        countryCodes.put("Senegal", "SN");
        countryCodes.put("Serbia", "RS");
        countryCodes.put("Seychelles", "SC");
        countryCodes.put("Sierra Leone", "SL");
        countryCodes.put("Singapore", "SG");
        countryCodes.put("Slovakia", "SK");
        countryCodes.put("Slovenia", "SI");
        countryCodes.put("Solomon Islands", "SB");
        countryCodes.put("Somalia", "SO");
        countryCodes.put("South Africa", "ZA");
        countryCodes.put("South Korea", "KR");
        countryCodes.put("South Sudan", "SS");
        countryCodes.put("Spain", "ES");
        countryCodes.put("Sri Lanka", "LK");
        countryCodes.put("Sudan", "SD");
        countryCodes.put("Suriname", "SR");
        countryCodes.put("Sweden", "SE");
        countryCodes.put("Switzerland", "CH");
        countryCodes.put("Syria", "SY");
        countryCodes.put("Taiwan", "TW");
        countryCodes.put("Tajikistan", "TJ");
        countryCodes.put("Tanzania", "TZ");
        countryCodes.put("Thailand", "TH");
        countryCodes.put("Timor-Leste", "TL");
        countryCodes.put("Togo", "TG");
        countryCodes.put("Tonga", "TO");
        countryCodes.put("Trinidad and Tobago", "TT");
        countryCodes.put("Tunisia", "TN");
        countryCodes.put("Turkey", "TR");
        countryCodes.put("Turkmenistan", "TM");
        countryCodes.put("Tuvalu", "TV");
        countryCodes.put("Uganda", "UG");
        countryCodes.put("Ukraine", "UA");
        countryCodes.put("United Arab Emirates", "AE");
        countryCodes.put("United Kingdom", "GB");
        countryCodes.put("United States of America", "US");
        countryCodes.put("Uruguay", "UY");
        countryCodes.put("Uzbekistan", "UZ");
        countryCodes.put("Vanuatu", "VU");
        countryCodes.put("Vatican City", "VA");
        countryCodes.put("Venezuela", "VE");
        countryCodes.put("Vietnam", "VN");
        countryCodes.put("Yemen", "YE");
        countryCodes.put("Zambia", "ZM");
        countryCodes.put("Zimbabwe","ZW");
    }
}
