package nimblix.in.HealthCareHub.helper;
/*
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MailHelper {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.template.reset-password-otp}")
    private String otpHtmlTemplate;


    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&-]+(?:\\.[a-zA-Z0-9_+&-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



    @Async
    public void sendOtpMail(
            String toEmail,
            String name,
            String otp,
            String subject
    ) {
        if (!isValidEmail(toEmail)) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            String body = otpHtmlTemplate;

            body = body.replace("{name}", name != null ? name : "User");
            body = body.replace("{otp}", otp != null ? otp : "XXXXXX");

            helper.setText(body, true); // HTML enabled
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Async
    public void sendTextMail(
            String toEmail,
            String subject,
            String body
    ) {
        if (!isValidEmail(toEmail)) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public static int getSixDigitRandomNumber() {
        return 100000 + new java.util.Random().nextInt(900000);
    }
}

*/
