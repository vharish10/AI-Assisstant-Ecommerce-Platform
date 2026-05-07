import { Link } from "react-router-dom";

const ProductCard = ({ product }) => {
  return (
    <div className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition duration-300">
      <img
        src={`${import.meta.env.VITE_SERVER_URL}${product.images[0]}`}
        alt={product.name}
        className="w-full h-52 object-cover"
      />

      <div className="p-4">
        <h2 className="text-lg font-bold">{product.name}</h2>

        <p className="text-gray-500 mt-2">
          ₹ {product.price}
        </p>

        <Link
                  to={`/product/${product.id}`}
                  className="inline-block mt-4 bg-blue-600 text-white px-4 py-2 rounded-lg"
                >
                  View Details
                </Link>
              </div>
            </div>
          );
        };

        export default ProductCard;