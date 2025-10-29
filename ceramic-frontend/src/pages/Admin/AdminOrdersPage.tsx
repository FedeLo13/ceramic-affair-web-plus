import { useCallback, useState } from "react";
import OrdersList from "../../components/OrderList/OrderList";
import { getAllPedidos, getAllPedidosEnviados, getAllPedidosNoEnviados } from "../../api/pedido";
import "./AdminOrdersPage.css";

export default function AdminOrdersPage() {
  const [filter, setFilter] = useState<"all" | "pending" | "shipped">("all");

  const fetcher = useCallback(async () => {
    if (filter === "all") return getAllPedidos();
    if (filter === "pending") return getAllPedidosNoEnviados();
    return getAllPedidosEnviados();
  }, [filter]);

  return (
    <div className="admin-orders-container">
      <div className="filter-bar">
        <label htmlFor="filter-select">Filter by:</label>
        <select
          id="filter-select"
          value={filter}
          onChange={(e) => setFilter(e.target.value as any)}
          className="filter-select"
        >
          <option value="all">All orders</option>
          <option value="pending">Pending</option>
          <option value="shipped">Shipped</option>
        </select>
      </div>

      <OrdersList
        fetchOrders={fetcher}
        title={`Orders — ${filter === "all" ? "All" : filter === "pending" ? "Pending" : "Shipped"}`}
        showClient={true}
      />
    </div>
  );
}