import api from "../api/axios";

export const getCart = async (userId) => {
  const response = await api.get("/cart", {
    params: { userId },
  });

  return response.data;
};

export const addToCart = async (userId, payload) => {
  const response = await api.post(
    `/cart/add?userId=${userId}`,
    payload
  );

  return response.data;
};

export const updateCart = async (userId, payload) => {
  const response = await api.put(
    `/cart/update?userId=${userId}`,
    payload
  );

  return response.data;
};

export const removeFromCart = async (userId, productId) => {
  const response = await api.delete(
    `/cart/remove?userId=${userId}&productId=${productId}`
  );

  return response.data;
};