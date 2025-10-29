import { useSearchParams } from "react-router-dom";

export default function Confirmacion() {
  const [searchParams] = useSearchParams();
  const status = searchParams.get("status");

  const mensaje = (() => {
    switch (status) {
      case "not_found":
        return "We couldn't find your subscription. Please try again later.";
      case "expired":
        return "The link has expired. Please request a new one.";
      case "subscribed":
        return "Your subscription has been confirmed!";
      case "unsubscribed":
        return "Your subscription has been successfully canceled.";
      case "verified":
        return "Registration verified successfully!";
      case "user_not_found":
        return "Registration verification failed. Please contact support.";
      case "token_expired":
        return "The verification link has expired. Please register again.";
      case "checkout_success":
        return "Thank you for your purchase! Your order has been successfully processed. You will receive a confirmation email shortly.";
      case "checkout_failed":
        return "Unfortunately, your payment could not be processed. Please try again or contact support if the issue persists.";
      default:
        return "An error occurred while processing your request.";
    }
  })();

  return (
    <div style={{ textAlign: "center", marginTop: "3rem" }}>
      <h1>{mensaje}</h1>
    </div>
  );
}
