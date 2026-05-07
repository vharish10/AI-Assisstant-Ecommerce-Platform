import { useEffect, useState } from "react";
import {
  getCart,
  removeFromCart,
} from "../services/cartService";

const Cart = () => {
  const [cart, setCart] = useState(null);

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    const data = await getCart(1);
    setCart(data);
  };

  const handleRemove = async (productId) => {
    await removeFromCart(1, productId);
    fetchCart();
  };
if (!cart) return <p>Loading...</p>;

  return (
    <div className="max-w-6xl mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">
        Your Cart
      </h1>

      {cart.items.map((item) => (
        <div
          key={item.productId}
          className="bg-white p-4 rounded-xl shadow mb-4 flex justify-between"
        >
          <div>
            <h2 className="font-bold">
              {item.name}
            </h2>

            <p>Qty: {item.quantity}</p>

            <p>₹ {item.price}</p>
          </div>
          <button
                      onClick={() => handleRemove(item.productId)}
                      className="bg-red-500 text-white px-4 py-2 rounded-lg"
                    >
                      Remove
                    </button>
                  </div>
                ))}

                <div className="text-right mt-6 text-2xl font-bold">
                  Total: ₹ {cart.totalPrice}
                </div>
              </div>
            );
          };

          export default Cart;