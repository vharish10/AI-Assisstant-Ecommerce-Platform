import { BrowserRouter } from "react-router-dom";
import AppRoutes from "./routes/AppRoutes";

import Navbar from "./components/common/Navbar";
import Footer from "./components/common/Footer";

import { Toaster } from "react-hot-toast";

function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <AppRoutes />

      <Footer />

      <Toaster position="top-right" />
    </BrowserRouter>
  );
}

export default App;