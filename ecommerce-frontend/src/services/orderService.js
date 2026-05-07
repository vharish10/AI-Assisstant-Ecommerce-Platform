import api from "../api/axios";

export const getOrders = async (userId) => {
  const response = await api.get(`/orders/user?userId=${userId}`);
  return response.data;
};

export const placeOrder = async (payload) => {
  const response = await api.post("/orders/checkout", payload);
  return response.data;
};

export const cancelOrder = async (orderId) => {
  const response = await api.put(`/orders/${orderId}/cancel`);
  return response.data;
};