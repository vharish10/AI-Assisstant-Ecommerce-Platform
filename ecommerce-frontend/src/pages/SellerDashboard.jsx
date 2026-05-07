const SellerDashboard = () => {
  return (
    <div className="max-w-7xl mx-auto p-4">
      <h1 className="text-4xl font-bold mb-6">
        Seller Dashboard
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white rounded-xl shadow p-6">
          <h2 className="text-gray-500">Revenue</h2>
          <p className="text-3xl font-bold mt-2">₹ 2,40,000</p>
        </div>

        <div className="bg-white rounded-xl shadow p-6">
          <h2 className="text-gray-500">Orders</h2>
          <p className="text-3xl font-bold mt-2">150</p>
        </div>
      </div>
    </div>
  );
};

export default SellerDashboard;