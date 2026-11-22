package com.finance.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.finance.model.FinMailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FinMailServiceImpl implements FinMailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public String sendMail(FinMailRequest finMailRequest) {
		log.debug("FINOVA Send Mail Service :: {}", finMailRequest.getTo());
		try {

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			helper.setTo(finMailRequest.getTo());
			helper.setSubject(finMailRequest.getSubject());
			// true indicates HTML content (FINOVA header + same message)
			helper.setText(buildEmailHtml(finMailRequest.getMessage()), true);

			javaMailSender.send(mimeMessage);

			log.info("Email sent successfully to {}", finMailRequest.getTo());
			return "Email sent successfully";
		} catch (MessagingException | MailException exception) {
			log.error("Failed to send email", exception);
			throw new RuntimeException("Unable to send email. Please try again later");
		}
	}

	/**
	 * Wraps the message with the FINOVA header (logo + name). Nothing else is
	 * added: the message itself is shown as it was sent.
	 */
	private String buildEmailHtml(String message) {

		String safeMessage = formatMessage(message);

		String emailBody = """
				<!DOCTYPE html>
				<html lang="en">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>FINOVA</title>
				</head>
				<body style="margin:0; padding:0; background-color:#f1f1f1; font-family:Arial, Helvetica, sans-serif;">
				    <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#f1f1f1; padding:24px 10px;">
				        <tr>
				            <td align="center">
				                <table role="presentation" width="600" cellpadding="0" cellspacing="0"
				                       style="width:100%; max-width:600px; background-color:#f7f7f7; border:1px solid #e6e6e6;">

				                    <!-- HEADER -->
				                    <tr>
				                        <td align="center" style="padding:8px 20px; background-color:#6d28d9;">
				                            <table role="presentation" cellpadding="0" cellspacing="0" align="center">
				                                <tr>
				                                    <td valign="middle">
				                                        {{LOGO}}
				                                    </td>
				                                    <td valign="middle" style="padding-left:8px;">
				                                        <span style="color:#ffffff; font-size:20px; font-weight:bold; letter-spacing:0.5px;">FIN<span style="color:#fbcfe8;">OVA</span></span>
				                                    </td>
				                                </tr>
				                            </table>
				                        </td>
				                    </tr>

				                    <!-- MESSAGE -->
				                    <tr>
				                        <td style="padding:20px 24px; color:#222222; font-size:14px; line-height:1.45; word-wrap:break-word; overflow-wrap:break-word;">
				                            {{MESSAGE}}
				                        </td>
				                    </tr>

				                </table>
				            </td>
				        </tr>
				    </table>
				</body>
				</html>
				""";

		return emailBody.replace("{{LOGO}}", buildLogoHtml(28, 17)).replace("{{MESSAGE}}", safeMessage);
	}

	/**
	 * Converts the message into compact HTML: every line is its own tight block,
	 * and any run of blank lines becomes one small gap (instead of big spacing).
	 */
	private String formatMessage(String message) {

		String[] lines = escapeHtml(message).replace("\r\n", "\n").replace("\r", "\n").trim().split("\n", -1);

		StringBuilder html = new StringBuilder();
		boolean previousBlank = false;

		for (String line : lines) {

			if (line.trim().isEmpty()) {
				if (!previousBlank) {
					html.append("<div style=\"height:6px; line-height:6px; font-size:0;\">&nbsp;</div>");
				}
				previousBlank = true;
			} else {
				html.append("<div style=\"margin:0; padding:0;\">").append(line.trim()).append("</div>");
				previousBlank = false;
			}
		}

		return html.toString();
	}

	/**
	 * FINOVA logo (gradient circle + white "F" + green leaf) in pure HTML/CSS.
	 */
	private String buildLogoHtml(int size, int fontSize) {

		int leaf = Math.round(size * 0.36f);
		int leafShiftX = -Math.round(size * 0.30f);
		int leafShiftY = -Math.round(size * 0.10f);

		return "<div style=\"font-size:0; line-height:0; white-space:nowrap;\">"
				+ "<div style=\"display:inline-block; vertical-align:middle; width:" + size + "px; height:" + size
				+ "px; line-height:" + size + "px; text-align:center; border-radius:50%; "
				+ "background-color:#c026d3; background-image:linear-gradient(135deg,#a855f7,#ec4899); "
				+ "border:2px solid #ffffff; box-sizing:border-box; color:#ffffff; font-family:Arial,Helvetica,sans-serif; "
				+ "font-size:" + fontSize + "px; font-weight:bold;\">F</div>"
				+ "<div style=\"display:inline-block; vertical-align:top; width:" + leaf + "px; height:" + leaf
				+ "px; margin-left:" + leafShiftX + "px; margin-top:" + leafShiftY + "px; " + "border-radius:0 " + leaf
				+ "px 0 " + leaf + "px; "
				+ "background-color:#22c55e; background-image:linear-gradient(135deg,#86efac,#22c55e);\"></div>"
				+ "</div>";
	}

	/**
	 * Escapes HTML characters to safely display email message text.
	 */
	private String escapeHtml(String value) {

		if (value == null) {
			return "";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	@Override
	public void sendMailWithAttachment(String to, String subject, String message, String fileName, byte[] attachment) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(message, false);
			helper.addAttachment(fileName, new ByteArrayResource(attachment), "text/csv");

			javaMailSender.send(mimeMessage);

			log.info("Email Sent Successfully :: {}", to);
		} catch (MessagingException | MailException e) {
			log.error("Failed to Send Monthly Report Email :: {}", to, e);
			throw new RuntimeException("Unable to Send Monthly Report Email", e);
		}
	}
}
