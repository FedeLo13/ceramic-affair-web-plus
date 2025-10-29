import { getPedidosByUserId } from "../../api/pedido";
import { useCallback } from "react";
import OrdersList from "../../components/OrderList/OrderList";
import { useAuth } from "../../context/AuthContext";

export default function UserOrdersPage() {
    const { userId } = useAuth();

    const fetcher = useCallback(async () => {
        if (!userId) return [];
        return getPedidosByUserId(userId.toString());
    }, [userId]);

    return (
    <div style={{ padding: 16 }}>
      <OrdersList fetchOrders={fetcher} title="Your Orders" showClient={false} />
    </div>
  );
}