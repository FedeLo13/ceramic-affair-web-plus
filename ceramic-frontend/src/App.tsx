import { Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./layout/MainLayout";
import About from "./pages/About/About";
import Pieces from "./pages/Pieces/Pieces";
import PieceDetail from "./pages/PieceDetail/PieceDetail";
import Contact from "./pages/Contact/Contact";
import FindMe from "./pages/FindMe/FindMe";
import AdminLogin from "./pages/Login/AdminLogin";
import AdminProductNew from "./pages/Admin/AdminProductNew";
import AdminProductEdit from "./pages/Admin/AdminProductEdit";
import AdminRoute from "./components/ProtectedRoutes/AdminRoute";
import AdminManageCategories from "./pages/Admin/AdminManageCategories";
import Confirmacion from "./pages/Confirmation/Confirmation";
import { GoogleReCaptchaProvider } from "react-google-recaptcha-v3";
import AdminFindMeEdit from "./pages/Admin/AdminFindMeEdit";
import AdminFindMeNew from "./pages/Admin/AdminFindMeNew";
import PoliticaPrivacidad from "./pages/PolíticaPrivacidad/PrivacyPolicy";
import UserLogin from "./pages/Login/UserLogin";
import UserRegister from "./pages/Register/UserRegister";
import UserRoute from "./components/ProtectedRoutes/UserRoute";
import PasswordChange from "./pages/Password/PasswordChange";
import ResetPassword from "./pages/Password/ResetPassword";
import ResetRequest from "./pages/Password/ResetRequest";
import CartPage from "./pages/CartPage/CartPage";
import CheckoutPage from "./pages/CheckoutPage/CheckoutPage";
import UserOrdersPage from "./pages/UserOrdersPage/UserOrdersPage";
import OrderDetails from "./pages/OrderDetails/OrderDetails";
import AdminOrdersPage from "./pages/Admin/AdminOrdersPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        {/* Rutas públicas */}
        <Route index element={<Navigate to="/pieces" />} />
        <Route path="about" element={<About />} />
        <Route path="pieces" element={<Pieces showFilters={false} />} />
        <Route path="search" element={<Pieces showFilters={true} />} />
        <Route path="pieces/:id" element={<PieceDetail />} />
        <Route path="contact" element={
          <GoogleReCaptchaProvider reCaptchaKey='6Ley9IwrAAAAANfsyFpChaZHQLShQWdi7UwYBWxR'>
            <Contact />
          </GoogleReCaptchaProvider>
        } />
        <Route path="find-me" element={<FindMe />} />
        <Route path="privacy-policy" element={<PoliticaPrivacidad />} />
        <Route path="cart" element={<CartPage />} />
        <Route path="checkout" element={<CheckoutPage />} />
        <Route path="user-register" element={
          <GoogleReCaptchaProvider reCaptchaKey='6Ley9IwrAAAAANfsyFpChaZHQLShQWdi7UwYBWxR'>
            <UserRegister />
          </GoogleReCaptchaProvider>
        } />
        <Route path="user-login" element={
          <GoogleReCaptchaProvider reCaptchaKey='6Ley9IwrAAAAANfsyFpChaZHQLShQWdi7UwYBWxR'>
            <UserLogin />
          </GoogleReCaptchaProvider>
        } />
          <Route path="reset-request" element={
          <GoogleReCaptchaProvider reCaptchaKey='6Ley9IwrAAAAANfsyFpChaZHQLShQWdi7UwYBWxR'>
            <ResetRequest />
          </GoogleReCaptchaProvider>
        } />
        <Route path="reset-password" element={<ResetPassword />} />
        {/* Rutas protegidas para usuarios */}
        <Route path="password-change" element={
          <GoogleReCaptchaProvider reCaptchaKey='6Ley9IwrAAAAANfsyFpChaZHQLShQWdi7UwYBWxR'>
            <UserRoute>
              <PasswordChange />
            </UserRoute>
          </GoogleReCaptchaProvider>
        } />
        <Route path="my-orders" element={
          <UserRoute>
            <UserOrdersPage />
          </UserRoute>
        } />
        <Route path="orders/:id" element={
          <UserRoute>
            <OrderDetails />
          </UserRoute>
        } />
        {/* Rutas de administración */}
        <Route path="admin-login" element={
          <GoogleReCaptchaProvider reCaptchaKey='6Ley9IwrAAAAANfsyFpChaZHQLShQWdi7UwYBWxR'>
            <AdminLogin />
          </GoogleReCaptchaProvider>
        } />
        <Route path="confirmation" element={<Confirmacion />} />
        {/* Rutas protegidas para administración */}
        <Route path="admin/products/new" element={
          <AdminRoute>
            <AdminProductNew />
          </AdminRoute>
        } />
        <Route path="admin/find-me/new" element={
          <AdminRoute>
            <AdminFindMeNew />
          </AdminRoute>
        } />
        <Route path="admin/products/edit/:id" element={
          <AdminRoute>
            <AdminProductEdit />
          </AdminRoute>
        } />
        <Route path="admin/find-me/edit/:id" element={
          <AdminRoute>
            <AdminFindMeEdit />
          </AdminRoute>
        } />
        <Route path="admin/categories" element={
          <AdminRoute>
            <AdminManageCategories />
          </AdminRoute>
        } />
        <Route path="admin/orders" element={
          <AdminRoute>
            <AdminOrdersPage />
          </AdminRoute>
        } />
      </Route>
    </Routes>
  );
}

export default App;