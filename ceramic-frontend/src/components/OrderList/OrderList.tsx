import { useEffect, useState } from "react";
import type { PedidoDTO } from "../../types/pedido.types"
import { useNavigate } from "react-router-dom";
import { getImagenById } from "../../api/imagenes";
import "./OrderList.css";

type Props = {
    fetchOrders: () => Promise<PedidoDTO[]>;
    title?: string;
    showClient?: boolean;
}

export default function OrderList({ fetchOrders, title = "Orders", showClient = true }: Props) {
    const [orders, setOrders] = useState<PedidoDTO[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const BASE_IMAGE_URL = 'http://localhost:8080/uploads/';

    useEffect(() => {
        let mounted = true;
        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await fetchOrders();

                // Obtener imagen del primer producto si existe
                const withImages = await Promise.all(
                    data.map(async (order) => {
                        const firstItem = order.items?.[0];
                        let imageUrl = "images/1068302.png"; // Placeholder
                        if (firstItem?.producto?.idsImagenes?.length > 0) {
                            try {
                                const img = await getImagenById(firstItem.producto.idsImagenes[0]);
                                imageUrl = img?.ruta?.startsWith("http")
                                    ? img.ruta
                                    : `${BASE_IMAGE_URL}${img.ruta}`;
                            } catch {
                                // Ignorar errores individuales de imagen
                            }
                        }
                        return { ...order, __previewImage: imageUrl } as PedidoDTO & { __previewImage: string };
                    })
                );

                if (!mounted) return;
                setOrders(withImages as unknown as PedidoDTO[]);
            } catch (err: any) {
                setError(err?.message ?? 'Error loading orders');
            } finally {
                if (mounted) setLoading(false);
            }
        };

        load();
        return () => { 
            mounted = false;
        };
    }, [fetchOrders]);

    if (loading) return <div className="orders-loading">Loading orders...</div>;
    if (error) return <div className="orders-error">Error: {error}</div>;
    if (!orders.length) return <div className="orders-empty">No orders found.</div>;

    return (
        <div className="orders-list">
            <div className="orders-container">
                <h2>{title}</h2>
                <ul>
                    {orders.map((order) => {
                        const first = order.items[0];
                        const moreCount = Math.max(0, (order.items?.length || 0) - 1);
                        const preview: string = (order as any).__previewImage || "images/1068302.png";
                        return (
                            <li key={order.id} className={`order-row ${order.enviado ? "shipped" : "pending"}`} onClick={() => navigate(`/orders/${order.id}`)}>
                                <div className="order-left">
                                    <img className="order-preview" src={preview} alt={first?.producto?.nombre ?? "preview"} />
                                    <div className="order-meta">
                                        <div className="order-title">
                                            <span className="order-product-name">{first?.producto?.nombre ?? "—"}</span>
                                            {moreCount > 0 && <span className="more">(and {moreCount} more)</span>}
                                        </div>
                                        <span className="order-id">#{order.id}</span>
                                        {showClient && <span className="order-client"> {order.pedido.nombreCliente} {order.pedido.apellidosCliente}</span>}
                                    </div>
                                </div>

                                <div className="order-right">
                                    <div className="order-amount">{order.total.toFixed(2)} €</div>
                                    <div className={`order-badge ${order.enviado ? "badge-shipped" : "badge-pending"}`}>
                                        {order.enviado ? "Shipped" : "Pending"}
                                    </div>
                                </div>
                            </li>
                        );
                    })}
                </ul>
            </div>
        </div>
    );
}