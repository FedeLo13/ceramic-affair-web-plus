import { useEffect, useState } from "react";
import { FaTrash } from "react-icons/fa";
import "./CartPage.css";
import { useAuth } from "../../context/AuthContext";
import type { CarritoDTO, CarritoItemDTO } from "../../types/carrito.types";
import { getGuestCart, removeItemFromGuestCart, type GuestCartItem } from "../../types/guestCart";
import { getCart, removeItemFromCart } from "../../api/carrito";
import { getImagenById } from "../../api/imagenes";
import { useNavigate } from "react-router-dom";
import { getProductoById } from "../../api/productos";

type CarritoItemConImagen = CarritoItemDTO & { imageUrl: string };
type CarritoItemConEstado = CarritoItemConImagen & { unavailable?: boolean };
type CarritoConImagen = Omit<CarritoDTO, "items"> & { items: CarritoItemConEstado[] };

type GuestCartItemConEstado = GuestCartItem & { unavailable?: boolean };
type GuestCart = { items: GuestCartItemConEstado[] };

type CartState =
  | { kind: "user"; cart: CarritoConImagen }
  | { kind: "guest"; cart: GuestCart }
  | { kind: "none" };

export default function CartPage() {
  const { isAuthenticated, userId } = useAuth();
  const [cart, setCart] = useState<CartState>({ kind: "none" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const BASE_IMAGE_URL = "http://localhost:8080/uploads/";

  // Cargar carrito
  useEffect(() => {
    const fetchCart = async () => {
      setLoading(true);
      setError("");
      try {
        if (isAuthenticated && userId) {
          const carrito = await getCart(userId.toString());
          const itemsConEstado = await Promise.all(
            carrito.items.map(async (item) => {
              let imageUrl = "images/1068302.png";
              if (item.producto.idsImagenes?.length > 0) {
                try {
                  const imagen = await getImagenById(item.producto.idsImagenes[0]);
                  imageUrl = imagen?.ruta.startsWith("http")
                    ? imagen.ruta
                    : `${BASE_IMAGE_URL}${imagen.ruta}`;
                } catch {
                  // Ignorar errores individuales de imagen
                }
              }
                // Comprobar disponibilidad
                let unavailable = false;
                try {
                  const productoActual = await getProductoById(item.producto.id);
                  if (!productoActual || productoActual.soldOut) {
                    unavailable = true;
                  }
                } catch {
                  unavailable = true; // Marcar como no disponible si hay error
                }

                return { ...item, imageUrl, unavailable };
            })
          );
          setCart({ kind: "user", cart: { ...carrito, items: itemsConEstado } });
        } else {
          const guestCart = getGuestCart();
          
          const itemsConEstado = await Promise.all(
            guestCart.items.map(async (item) => {
              let unavailable = false;
              try {
                const productoActual = await getProductoById(item.productId);
                if (!productoActual || productoActual.soldOut) {
                  unavailable = true;
                }
              } catch {
                unavailable = true; // Marcar como no disponible si hay error
              }

              return { ...item, unavailable };
            })
          );

          setCart({ kind: "guest", cart: { items: itemsConEstado } });
        }
      } catch {
        setError("Error loading cart.");
      } finally {
        setLoading(false);
      }
    };

    fetchCart();
  }, [isAuthenticated, userId]);

  // Eliminar producto
  const handleRemove = async (productId: number) => {
    try {
      if (cart.kind === "user" && isAuthenticated && userId) {
        await removeItemFromCart(userId.toString(), productId.toString());
        setCart((prev) =>
          prev.kind !== "user"
            ? prev
            : {
                kind: "user",
                cart: {
                  ...prev.cart,
                  items: prev.cart.items.filter((i) => i.producto.id !== productId),
                },
              }
        );
      } else if (cart.kind === "guest") {
        removeItemFromGuestCart(productId);
        setCart((prev) =>
          prev.kind !== "guest"
            ? prev
            : {
                kind: "guest",
                cart: {
                  ...prev.cart,
                  items: prev.cart.items.filter((i) => i.productId !== productId),
                },
              }
        );
      }
    } catch {
      setError("Error removing item.");
    }
  };

  // Calcular total
  const total =
    cart.kind === "none"
      ? 0
      : cart.kind === "user"
      ? cart.cart.items.reduce((sum, i) => sum + i.precioUnitario, 0)
      : cart.cart.items.reduce((sum, i) => sum + i.precio, 0);

  const hasUnavailableItems =
    cart.kind !== "none" &&
    cart.cart.items.some((item) => item.unavailable);

  return (
    <div className="cart-page">
      <h1>Your Cart</h1>

      {loading && <p>Loading cart...</p>}
      {error && <p className="cart-error">{error}</p>}

      {!loading && !error && (
        <>
          {cart.kind === "none" || cart.cart.items.length === 0 ? (
            <p className="empty-cart-text">Your cart is empty.</p>
          ) : (
            <div className="cart-container">
              <ul className="cart-list">
                {cart.kind === "user"
                  ? cart.cart.items.map((item) => (
                      <li key={item.producto.id} className={`cart-item ${item.unavailable ? "unavailable" : ""}`}>
                        <img
                          src={item.imageUrl}
                          alt={item.producto.nombre}
                          className="cart-item-image"
                        />
                        <div className="cart-item-info">
                          <h3>{item.producto.nombre}</h3>
                          <p className="cart-category">{item.producto.nombreCategoria}</p>
                        </div>
                        <div className="cart-item-right">
                          <div className="cart-item-price">
                            {item.precioUnitario.toFixed(2)} €
                          </div>
                          {item.unavailable && (
                            <p className="cart-unavailable-text">Product is no longer available!</p>
                          )}
                        </div>
                        <button
                          className="cart-remove-btn"
                          onClick={() => handleRemove(item.producto.id)}
                        >
                          <FaTrash />
                        </button>
                      </li>
                    ))
                  : cart.cart.items.map((item) => (
                      <li key={item.productId} className={`cart-item ${item.unavailable ? "unavailable" : ""}`}>
                        <img
                          src={item.imagen}
                          alt={item.nombre}
                          className="cart-item-image"
                        />
                        <div className="cart-item-info">
                          <h3>{item.nombre}</h3>
                          <p className="cart-category">{item.nombreCategoria}</p>
                        </div>
                        <div className="cart-item-right">
                          <div className="cart-item-price">{item.precio.toFixed(2)} €</div>
                          {item.unavailable && (
                            <p className="cart-unavailable-text">Product is no longer available!</p>
                          )}
                        </div>
                        <button
                          className="cart-remove-btn"
                          onClick={() => handleRemove(item.productId)}
                        >
                          <FaTrash />
                        </button>
                      </li>
                    ))}
              </ul>

              <div className="cart-summary">
                <div className="cart-total">
                  <span>Total:</span>
                  <span>{total.toFixed(2)} €</span>
                </div>
                <button className="checkout-btn" onClick={() => navigate("/checkout")} disabled={hasUnavailableItems}>Proceed to Checkout</button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}