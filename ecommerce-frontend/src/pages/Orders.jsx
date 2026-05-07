import { useEffect, useState } from "react";
import {
  getOrders,
  cancelOrder,
} from "../services/orderService";

const Orders = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    const data = await getOrders(1);
    setOrders(data);
  };

  const handleCancel = async (orderId) => {
    await cancelOrder(orderId);
    fetchOrders();
  };

return (
    <div className="max-w-6xl mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">
        My Orders
      </h1>

      {orders.map((order) => (
        <div
          key={order.orderId}
          className="bg-white p-4 rounded-xl shadow mb-4"
        >
          <h2 className="font-bold">
            Order #{order.orderId}
          </h2>

          <p>Status: {order.status}</p>

          <p>Total: ₹ {order.totalAmount}</p>

          <button
            onClick={() => handleCancel(order.orderId)}
            className="mt-4 bg-red-500 text-white px-4 py-2 rounded-lg"
          >
           Cancel Order
                    </button>
                  </div>
                ))}
              </div>
            );
          };

          export default Orders;