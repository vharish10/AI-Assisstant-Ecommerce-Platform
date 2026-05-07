import { useEffect, useState } from "react";
import {
  getWishlist,
  removeFromWishlist,
} from "../services/wishlistService";

const Wishlist = () => {
  const [wishlist, setWishlist] = useState(null);

  useEffect(() => {
    fetchWishlist();
  }, []);

  const fetchWishlist = async () => {
    const data = await getWishlist(1);
    setWishlist(data);
  };

  const handleRemove = async (productId) => {
    await removeFromWishlist({
      userId: 1,
      productId,
    });
fetchWishlist();
  };

  if (!wishlist) return <p>Loading...</p>;

  return (
    <div className="max-w-6xl mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6">
        Wishlist
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {wishlist.products.map((product) => (
          <div
            key={product.id}
            className="bg-white rounded-xl shadow-md p-4"
          >
            <img
              src={`${import.meta.env.VITE_SERVER_URL}${product.images[0]}`}
              alt={product.name}
              className="h-52 w-full object-cover rounded-lg"
            />

            <h2 className="mt-3 font-bold">
              {product.name}
            </h2>
            <button
                          onClick={() => handleRemove(product.id)}
                          className="mt-4 bg-red-500 text-white px-4 py-2 rounded-lg"
                        >
                          Remove
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              );
            };

            export default Wishlist;