import { useEffect, useState } from "react";

import { searchProducts } from "../services/productService";

import ProductCard from "../components/product/ProductCard";

const Home = () => {

  // Products State
  const [products, setProducts] = useState([]);

  // Loading State
  const [loading, setLoading] = useState(false);

  // Filters State
  const [filters, setFilters] = useState({
    keyword: "",
    category: "",
    brand: "",
    minPrice: "",
    maxPrice: "",
    sortBy: "price",
    sortDir: "asc",
  });

  // Initial Load
  useEffect(() => {
    fetchProducts();
  }, []);

  // Fetch Products
  const fetchProducts = async () => {
    try {
      setLoading(true);

      const data = await searchProducts({
        ...filters,
        page: 0,
        size: 12,
      });

      setProducts(data.products);

    } catch (error) {
      console.log(error);

    } finally {
      setLoading(false);
    }
  };

  // Handle Input Change
  const handleChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div className="max-w-7xl mx-auto p-4">

      {/* Heading */}
      <h1 className="text-3xl font-bold mb-6">
        Latest Products
      </h1>

      {/* Search + Filters */}
      <div className="bg-white p-4 rounded-xl shadow-md mb-8">

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-4">

          {/* Search */}
          <input
            type="text"
            name="keyword"
            placeholder="Search products..."
            value={filters.keyword}
            onChange={handleChange}
            className="border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Category */}
          <input
            type="text"
            name="category"
            placeholder="Category"
            value={filters.category}
            onChange={handleChange}
            className="border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Brand */}
          <input
            type="text"
            name="brand"
            placeholder="Brand"
            value={filters.brand}
            onChange={handleChange}
            className="border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Min Price */}
          <input
            type="number"
            name="minPrice"
            placeholder="Min Price"
            value={filters.minPrice}
            onChange={handleChange}
            className="border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Max Price */}
          <input
            type="number"
            name="maxPrice"
            placeholder="Max Price"
            value={filters.maxPrice}
            onChange={handleChange}
            className="border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Sorting */}
          <select
            name="sortDir"
            value={filters.sortDir}
            onChange={handleChange}
            className="border p-3 rounded-lg outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="asc">
              Price Low → High
            </option>

            <option value="desc">
              Price High → Low
            </option>
          </select>

        </div>

        {/* Search Button */}
        <button
          onClick={fetchProducts}
          className="mt-4 bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition"
        >
          Search
        </button>

      </div>

      {/* Loading */}
      {loading ? (
        <div className="text-center text-lg font-semibold">
          Loading Products...
        </div>
      ) : (

        /* Product Grid */
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">

          {products.length > 0 ? (
            products.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
              />
            ))
          ) : (
            <p className="text-gray-500">
              No products found
            </p>
          )}

        </div>
      )}

    </div>
  );
};

export default Home;