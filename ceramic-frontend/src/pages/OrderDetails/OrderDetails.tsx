// src/pages/orders/OrderDetails.tsx
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { PedidoDTO } from "../../types/pedido.types";
import { getPedidoById, setPedidoEnviado } from "../../api/pedido";
import type { PagoDTO } from "../../types/pago.types";
import { useAuth } from "../../context/AuthContext";
import { getPagoByPedidoId } from "../../api/pago";
import "./OrderDetails.css";
import { getImagenById } from "../../api/imagenes";

export default function OrderDetails() {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<PedidoDTO | null>(null);
  const [payment, setPayment] = useState<PagoDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [orderImages, setOrderImages] = useState<Record<number, string>>({});

  const { hasRole } = useAuth();

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      if (!id) return;
      setLoading(true);
      try {
        const p = await getPedidoById(Number(id));
        const pago = await getPagoByPedidoId(Number(id));
        if (mounted) {
          setOrder(p);
          setPayment(pago);
        }
      } catch (e) {
        console.error("Error loading order details:", e);
      } finally {
        if (mounted) setLoading(false);
      }
    };
    load();
    return () => { mounted = false; };
  }, [id]);

  useEffect(() => {
    const fetchImages = async () => {
      if (!order) return;
      const BASE_URL = "http://localhost:8080/uploads/";
      const newImages: Record<number, string> = {};

      await Promise.all(
        order.items.map(async (it) => {
          if (it.producto.idsImagenes?.length > 0) {
            try {
              const imgData = await getImagenById(it.producto.idsImagenes[0]);
              newImages[it.producto.id] = imgData?.ruta?.startsWith("http")
                ? imgData.ruta
                : `${BASE_URL}${imgData.ruta}`;
            } catch {
              newImages[it.producto.id] = "images/1068302.png";
            }
          } else {
            newImages[it.producto.id] = "images/1068302.png";
          }
        })
      );

      setOrderImages(newImages);
    };

    fetchImages();
  }, [order]);

  const handleMarkAsSent = async () => {
    if (!order) return;
    try {
      setUpdating(true);
      await setPedidoEnviado(order.id, !order.enviado);
      setOrder({ ...order, enviado: !order.enviado });
    } catch (e) {
      console.error("Error updating order status:", e);
    } finally {
      setUpdating(false);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (!order) return <p>Order not found</p>;

  return (
    <div className="order-details-container">
      <h2>Order #{order.id}</h2>

      <div className="order-details-form">
        <div className="order-details-form-row">
          <div className="order-detailsform-group">
            <label>Customer Name</label>
            <input
              readOnly
              value={`${order.pedido.nombreCliente} ${order.pedido.apellidosCliente}`}
            />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input readOnly value={order.pedido.emailCliente} />
          </div>
        </div>

        <div className="order-details-form-row">
          <div className="order-details-form-group">
            <label>Province</label>
            <input readOnly value={order.pedido.provincia} />
          </div>
          <div className="order-details-form-group">
            <label>City</label>
            <input readOnly value={order.pedido.ciudad} />
          </div>
          <div className="order-details-form-group">
            <label>Postal Code</label>
            <input readOnly value={order.pedido.codigoPostal} />
          </div>
        </div>

        <div className="order-details-form-group">
          <label>Address</label>
          <input readOnly value={order.pedido.direccion} />
        </div>

        <div className="order-details-form-row">
          <div className="order-details-form-group">
            <label>Payment Method</label>
            <input readOnly value={payment?.tipoPago ?? "—"} />
          </div>
          <div className="order-details-form-group">
            <label>Total</label>
            <input readOnly value={`${order.total.toFixed(2)} €`} />
          </div>
          <div className="order-detailsform-group">
            <label>Status</label>
            <input
              readOnly
              value={order.enviado ? "Shipped" : "Pending"}
              className={order.enviado ? "status-shipped" : "status-pending"}
            />
          </div>
        </div>

        {hasRole("ADMIN") && (
          <div className="admin-actions">
            <button
              onClick={handleMarkAsSent}
              disabled={updating}
              className={`admin-btn ${order.enviado ? "revert" : "mark"}`}
            >
              {updating
                ? "Updating..."
                : order.enviado
                ? "Mark as Pending"
                : "Mark as Shipped"}
            </button>
          </div>
        )}
      </div>

      <h3>Products</h3>
      <ul className="order-products-list">
        {order.items.map((it, idx) => (
          <li key={idx} className="order-product-item">
            <img
              src={orderImages[it.producto.id] || "images/1068302.png"}
              alt={it.producto.nombre}
            />
            <div className="product-info">
              <h4>{it.producto.nombre}</h4>
              <p className="price">{it.precioUnitario.toFixed(2)} €</p>
              <span className="category">{it.producto.nombreCategoria}</span>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
