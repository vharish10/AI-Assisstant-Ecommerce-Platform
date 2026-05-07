import api from "../api/axios";

export const searchProducts = async (params) => {
  const response = await api.get("/products/search", {
    params,
  });

  return response.data;
};

export const getProductById = async (id, userId) => {
  const response = await api.get(`/products/${id}`, {
    params: { userId },
  });

  return response.data;
};

export const createProduct = async (sellerId, formData) => {
  const response = await api.post(
    `/products?sellerId=${sellerId}`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return response.data;
};