import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import toast from "react-hot-toast";

import { getProductById } from "../services/productService";
import { addToCart } from "../services/cartService";

const ProductDetails = () => {
  const { id } = useParams();

  const [product, setProduct] = useState(null);

  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    fetchProduct();
  }, []);

  const fetchProduct = async () => {
    try {
      const data = await getProductById(id, 1);
      setProduct(data);
    } catch (error) {
      console.log(error);
    }
  };

  const increaseQuantity = () => {
    setQuantity((prev) => prev + 1);
  };

  const decreaseQuantity = () => {
    if (quantity > 1) {
      setQuantity((prev) => prev - 1);
    }
  };

  const handleAddToCart = async () => {
    try {
      await addToCart(1, {
        productId: product.id,
        quantity: quantity,
      });

      toast.success("Added to cart successfully");

    } catch (error) {
      console.log(error);

      toast.error(
        error?.response?.data ||
        "Something went wrong"
      );
    }
  };

  if (!product) return <p>Loading...</p>;

  return (
    <div className="max-w-7xl mx-auto p-4 grid md:grid-cols-2 gap-10">
      {/* Product Image */}
      <img
        src={`${import.meta.env.VITE_SERVER_URL}${product.images[0]}`}
        alt={product.name}
        className="w-full rounded-xl"
      />

      {/* Product Info */}
      <div>
        <h1 className="text-4xl font-bold">
          {product.name}
        </h1>

        <p className="mt-4 text-gray-600">
          {product.description}
        </p>

        <p className="mt-4 text-3xl font-bold text-blue-600">
          ₹ {product.price}
        </p>

        {/* Quantity Selector */}
        <div className="mt-6 flex items-center gap-4">
          <span className="text-lg font-semibold">
            Quantity:
          </span>

          <div className="flex items-center border rounded-lg overflow-hidden">
            <button
              onClick={decreaseQuantity}
              className="px-4 py-2 bg-gray-200 hover:bg-gray-300"
            >
              -
            </button>

            <span className="px-6 py-2 text-lg">
              {quantity}
            </span>

            <button
              onClick={increaseQuantity}
              className="px-4 py-2 bg-gray-200 hover:bg-gray-300"
            >
              +
            </button>
          </div>
        </div>

        {/* Add To Cart */}
        <button
          onClick={handleAddToCart}
          className="mt-6 bg-black text-white px-6 py-3 rounded-lg hover:bg-gray-800 transition"
        >
          Add To Cart
        </button>
      </div>
    </div>
  );
};

export default ProductDetails;