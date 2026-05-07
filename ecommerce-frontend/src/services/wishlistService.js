import api from "../api/axios";

export const getWishlist = async (userId) => {
  const response = await api.get(`/wishlist?userId=${userId}`);
  return response.data;
};

export const addToWishlist = async (payload) => {
  const response = await api.post("/wishlist/add", payload);
  return response.data;
};

export const removeFromWishlist = async (payload) => {
  const response = await api.delete("/wishlist/remove", {
    data: payload,
  });

  return response.data;
};