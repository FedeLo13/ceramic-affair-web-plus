import { useEffect, useState } from "react";
import "./CheckoutPage.css";
import { FaCreditCard, FaMobileAlt } from "react-icons/fa";
import { useAuth } from "../../context/AuthContext";
import type { TipoPago } from "../../types/pago.types";
import { checkout } from "../../api/checkout";
import type { CheckoutDTO } from "../../types/checkout.types";
import { clearGuestCart, getGuestCart } from "../../types/guestCart";
import type { CarritoItemDTO } from "../../types/carrito.types";
import { getProductoById } from "../../api/productos";
import { useNavigate } from "react-router-dom";

export default function CheckoutPage() {
  const { isAuthenticated, userId, userEmail } = useAuth();
  const [form, setForm] = useState({
    nombreCliente: "",
    apellidosCliente: "",
    emailCliente: "",
    provincia: "",
    ciudad: "",
    codigoPostal: "",
    direccion: "",
  });

  const [showModal, setShowModal] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState<"bizum" | "card" | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated && userEmail) {
      setForm((prev) => ({ ...prev, emailCliente: userEmail }));
    }
  }, [isAuthenticated, userEmail]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleOpenModal = (method: "bizum" | "card") => {
    setPaymentMethod(method);
    setShowModal(true);
  }

  const handleCloseModal = () => {
    setShowModal(false);
    setPaymentMethod(null);
  };

  const handleConfirmPayment = async (cancelled = false) => {
    if (!paymentMethod) return;

    setIsProcessing(true);

    const tipoPago: TipoPago = paymentMethod === "bizum" ? "BIZUM" : "TARJETA";
    const total = cancelled ? -1 : 100; // Placeholder

    let checkoutData: CheckoutDTO;

    try {
      if (isAuthenticated && userId) {
        // Caso 1: Usuario autenticado
        checkoutData = {
          usuarioId: userId,
          items: [], // El backend obtendrá los items del carrito del usuario
          total,
          tipoPago,
          pedido: form,
        };
      } else {
        // Caso 2: Usuario no autenticado
        const guestCart = getGuestCart();
        const cartItems: CarritoItemDTO[] = [];

        for (const item of guestCart.items) {
          try {
            const producto = await getProductoById(item.productId);
            cartItems.push({
              producto,
              precioUnitario: item.precio,
            });
          } catch (error) {
            console.error(`Error fetching product with ID ${item.productId}:`, error);
          }
        }

        const totalInvitado = cancelled
          ? -1
          : cartItems.reduce((sum, item) => sum + item.precioUnitario, 0);

        checkoutData = {
          usuarioId: null as any, // Usuario invitado
          items: cartItems,
          total: totalInvitado,
          tipoPago,
          pedido: form,
        };
      }

      const response = await checkout(checkoutData);

      if (response && response.estado === "SUCCESS") {
        // Limpiar carrito invitado si es necesario
        if (!isAuthenticated || !userId) {
          clearGuestCart();
        }

        // Redirigir al éxito
        navigate("/confirmation?status=checkout_success");
      } else {
        // Manejar error en el pago
        console.error("Payment failed:", response);
        navigate("/confirmation?status=checkout_failed");
      }
    } catch (error) {
      console.error("Error during checkout:", error);
      navigate("/confirmation?status=checkout_failed");
    } finally {
      setIsProcessing(false);
      setShowModal(false);
    }
  };

  const isFormComplete = Object.values(form).every((value) => value.trim() !== "");

  return (
    <>
      <form className="checkout-form">
        <h2>Shipping Information</h2>

        <div className="checkout-form-row">
            <div className="checkout-form-group">
                <label>Name</label>
                <input name="nombreCliente" value={form.nombreCliente} onChange={handleChange} required />
            </div>

            <div className="checkout-form-group">
                <label>Last Name</label>
                <input name="apellidosCliente" value={form.apellidosCliente} onChange={handleChange} required />
            </div>
        </div>

        <div className="checkout-form-row">
            <div className="checkout-form-group">
            <label>Province</label>
            <input name="provincia" value={form.provincia} onChange={handleChange} required />
            </div>

            <div className="checkout-form-group">
            <label>City</label>
            <input name="ciudad" value={form.ciudad} onChange={handleChange} required />
            </div>

            <div className="checkout-form-group">
            <label>Postal Code</label>
            <input
                type="text"
                name="codigoPostal"
                value={form.codigoPostal}
                onChange={handleChange}
                required
            />
            </div>
        </div>


        <div className="checkout-form-group">
            <label>Address</label>
            <input
            type="text"
            name="direccion"
            value={form.direccion}
            onChange={handleChange}
            required
            />
        </div>

        <div className="checkout-form-group">
            <label>Email</label>
            <input
            type="email"
            name="emailCliente"
            value={form.emailCliente}
            onChange={handleChange}
            required
            readOnly={isAuthenticated}
            />
        </div>

        <div className="checkout-buttons">
            <button 
              type="button" 
              className="checkout-btn bizum" 
              onClick={() => handleOpenModal("bizum")} 
              disabled={!isFormComplete || isProcessing}
            >
              <FaMobileAlt size={20} style={{ marginRight: "0.5rem" }} />
              Pay with Bizum
            </button>
            <button 
              type="button" 
              className="checkout-btn card"
              onClick={() => handleOpenModal("card")} 
              disabled={!isFormComplete || isProcessing}
            >
              <FaCreditCard size={20} style={{ marginRight: "0.5rem" }} />
              Pay with Card
            </button>
        </div>
      </form>

        {showModal && (
        <div className="checkout-modal-overlay" onClick={handleCloseModal}>
          <div
            className="checkout-modal"
            onClick={(e) => e.stopPropagation()} // evita cerrar si clicas dentro
          >
            <h3>Confirm your purchase</h3>
            <p>
              You’re about to pay using{" "}
              <strong>{paymentMethod === "bizum" ? "Bizum" : "Card"}</strong>.
            </p>

            <div className="checkout-modal-actions">
              <button 
                onClick={() => handleConfirmPayment(true)} 
                className="modal-btn cancel"
                disabled={isProcessing}
              >
                Cancel
              </button>
              <button 
                onClick={() => handleConfirmPayment(false)} 
                className="modal-btn confirm"
                disabled={isProcessing}
              >
                {isProcessing ? "Processing..." : "Confirm"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}