import { Link } from "react-router-dom";
import { ShoppingCart, Heart } from "lucide-react";

const Navbar = () => {
  return (
    <nav className="bg-white shadow-md p-4 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto flex justify-between items-center">
        <Link to="/" className="text-2xl font-bold text-blue-600">
          ShopEase
        </Link>

        <div className="flex gap-6 items-center">
          <Link to="/orders">Orders</Link>

          <Link to="/wishlist">
            <Heart />
          </Link>

          <Link to="/cart">
            <ShoppingCart />
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;