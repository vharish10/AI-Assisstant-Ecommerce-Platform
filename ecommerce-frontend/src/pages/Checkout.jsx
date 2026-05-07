import { placeOrder } from "../services/orderService";

const Checkout = () => {
  const handleCheckout = async () => {
    try {
      await placeOrder({
        userId: 1,
        addressId: 1,
      });

      alert("Order placed successfully");
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="max-w-3xl mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">
        Checkout
      </h1>

      <button
        onClick={handleCheckout}
        className="bg-green-600 text-white px-6 py-3 rounded-lg"
      >
      Place Order
            </button>
          </div>
        );
      };

      export default Checkout;