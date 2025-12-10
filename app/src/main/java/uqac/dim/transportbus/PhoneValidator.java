package uqac.dim.transportbus;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneValidator {

    // Méthode pour valider un numéro de téléphone
    public static boolean isValidPhoneNumber(String phoneNumber, String region) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, region);
            return phoneUtil.isValidNumber(number); // Vérifie la validité du numéro
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false; // Si une exception est lancée, le numéro est invalide
        }
    }

    // Méthode pour formater un numéro de téléphone en format E.164
    public static String formatToE164(String phoneNumber, String region) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, region);
            return phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164); // Format E.164
        } catch (NumberParseException e) {
            e.printStackTrace();
            return null; // Si une exception est lancée, retourne null
        }
    }
}
