import { useEffect, useRef, useState } from "react";
import { NavLink } from "react-router-dom";
import { FaInstagram, FaBars, FaCrown, FaUserCircle, FaShoppingCart, FaTrash } from "react-icons/fa";
import "./Header.css";
import { useAuth } from "../../context/AuthContext";
import type { CarritoDTO, CarritoItemDTO } from "../../types/carrito.types";
import { getCart, removeItemFromCart } from "../../api/carrito";
import { getImagenById } from "../../api/imagenes";
import { getGuestCart, removeItemFromGuestCart, type GuestCartItem } from "../../types/guestCart";

type CarritoItemConImagen = CarritoItemDTO & { imageUrl: string };

type CarritoConImagen = Omit<CarritoDTO, 'items'> & { items: CarritoItemConImagen[] };
type GuestCart = { items: GuestCartItem[] };

type CartState = 
    | { kind: "user"; cart: CarritoConImagen }
    | { kind: "guest"; cart: GuestCart }
    | { kind: "none" };

export default function Header() {
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const [adminMenuOpen, setAdminMenuOpen] = useState(false);
    const [userDropdownOpen, setUserDropdownOpen] = useState(false);
    const userDropdownRef = useRef<HTMLDivElement | null>(null);
    const [cartDropdownOpen, setCartDropdownOpen] = useState(false);
    const [cart, setCart] = useState<CartState>({ kind: "none" });
    const [cartLoading, setCartLoading] = useState(false);
    const [cartError, setCartError] = useState("");
    const cartDropdownRef = useRef<HTMLDivElement | null>(null);
    const { hasRole, isAuthenticated, userEmail, logout, userId } = useAuth();

    const BASE_IMAGE_URL = "http://localhost:8080/uploads/";

    // Evitar scroll cuando la sidebar está abierta
    useEffect(() => {
        if (sidebarOpen) {
            document.body.classList.add("no-scroll");
        } else {
            document.body.classList.remove("no-scroll");
        }
    }, [sidebarOpen]);

    // Cerrar cualquier dropdown al hacer clic fuera
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                userDropdownRef.current &&
                !userDropdownRef.current.contains(event.target as Node)
            ) {
                setUserDropdownOpen(false);
            }
            if (
                cartDropdownRef.current &&
                !cartDropdownRef.current.contains(event.target as Node)
            ) {
                setCartDropdownOpen(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    // Cargar el carrito cuando se abre el dropdown
    useEffect(() => {
        const fetchCart = async () => {
            setCartLoading(true);
            setCartError("");

            try {
                if (isAuthenticated && userId) {
                    // Usuario logueado -> usar API del backend
                    const carrito = await getCart(userId.toString());

                    // Obtener imágenes para cada ítem del carrito
                    const itemsConImagenes = await Promise.all(
                        carrito.items.map(async (item) => {
                            let imageUrl = "images/1068302.png"; // Imagen por defecto
                            if (item.producto.idsImagenes?.length > 0) {
                                try {
                                    const imagen = await getImagenById(item.producto.idsImagenes[0]);
                                    imageUrl = imagen?.ruta.startsWith("http")
                                        ? imagen.ruta
                                        : `${BASE_IMAGE_URL}${imagen.ruta}`;
                                } catch (error) {
                                    console.error("Error fetching image:", error);
                                }
                            }
                            return { ...item, imageUrl };
                        }
                    ));

                    setCart({ kind: "user", cart: { ...carrito, items: itemsConImagenes } });
                } else {
                    // Usuario invitado -> usar localStorage
                    const guestCart = getGuestCart();
                    setCart({ kind: "guest", cart: guestCart });
                }
            } catch (error) {
                console.error("Error loading cart:", error);
                setCartError("Error loading cart.");
            } finally {
                setCartLoading(false);
            }
        };

        if (cartDropdownOpen) {
            fetchCart();
        }
    }, [cartDropdownOpen, isAuthenticated, userId]);

    // Eliminar producto del carrito
    const handleRemoveFromCart = async (productId: number) => {
        try {
            setCartLoading(true);
            setCartError("");
            
            if (cart.kind === "user" && isAuthenticated && userId) {
                // Usuario logueado -> usar API del backend
                await removeItemFromCart(userId.toString(), productId.toString());
            
                // Actualizar el carrito localmente
                setCart((prevCart) => {
                    if (prevCart.kind !== "user") return prevCart;
                    return {
                        kind: "user",
                        cart: {
                            ...prevCart.cart,
                            items: prevCart.cart.items.filter((item) => item.producto.id !== productId)
                        }
                    };
                });
            } else if (cart.kind === "guest") {
                // Usuario invitado -> usar localStorage
                removeItemFromGuestCart(productId);

                // Actualizar el carrito localmente
                setCart((prevCart) => {
                    if (prevCart.kind !== "guest") return prevCart;
                    return {
                        kind: "guest",
                        cart: {
                            ...prevCart.cart,
                            items: prevCart.cart.items.filter((item) => item.productId !== productId)
                        }
                    };
                });
            }
        } catch (error) {
            console.error("Error removing item from cart:", error);
            setCartError("Error removing item from cart.");
        } finally {
            setCartLoading(false);
        }
    };
    
    return (
        <>
            {/* Header*/}
            <header className="header">
                {/*Logo de Ceramic Affair */}
                <div className="logo">
                    <img src="/images/CERAMIC_AFFAIR_logo.png" alt="Ceramic Affair Logo" />
                </div>

                {/*Enlaces de navegación */}
                <div className="nav-container">
                    <nav className="nav-links">
                        <NavLink to="/about">About</NavLink>
                        <NavLink to="/pieces">Pieces</NavLink>
                        <NavLink to="/search">Search</NavLink>
                        <NavLink to="/contact">Contact</NavLink>
                        <NavLink to="/find-me">Find Me</NavLink>
                    </nav>
                </div>
            </header>

            {/* Sidebar */}
            <div className={`sidebar ${sidebarOpen ? "open" : ""}`}>
                {/* Imagen del sidebar */}
                <div className="sidebar-img">
                    <img src="/images/CERAMIC_AFFAIR_logo.png" alt="Sidebar Image" />
                </div>
                <div className="sidebar-content">
                    <NavLink to="/about" onClick={() => setSidebarOpen(false)}>About</NavLink>
                    <NavLink to="/pieces" onClick={() => setSidebarOpen(false)}>Pieces</NavLink>
                    <NavLink to="/search" onClick={() => setSidebarOpen(false)}>Search</NavLink>
                    <NavLink to="/contact" onClick={() => setSidebarOpen(false)}>Contact</NavLink>
                    <NavLink to="/find-me" onClick={() => setSidebarOpen(false)}>Find Me</NavLink>
                    <a href="https://www.instagram.com/ceramic_affair/" target="_blank" rel="noopener noreferrer" className="instagram-icon" onClick={() => setSidebarOpen(false)}>
                        <FaInstagram size={24} />
                    </a>

                    {/* Admin Menu (solo visible si tiene rol ADMIN) */}
                    {hasRole("ADMIN") && (
                        <div className="admin-menu">
                            <button
                                className="admin-toggle" 
                                onClick={() => setAdminMenuOpen(!adminMenuOpen)}>
                                <FaCrown size={24} /> Admin Menu
                            </button>
                            {adminMenuOpen && (
                                <div className="sidebar-admin-dropdown">
                                    <NavLink to="/admin/products/new" onClick={() => setSidebarOpen(false)}>Add Pieces</NavLink>
                                    <NavLink to="/admin/find-me/new" onClick={() => setSidebarOpen(false)}>Add Find Me Post</NavLink>
                                    <NavLink to="/admin/categories" onClick={() => setSidebarOpen(false)}>Manage Categories</NavLink>
                                    <NavLink to="/admin/orders" onClick={() => setSidebarOpen(false)}>Manage Orders</NavLink>
                                    <button className="logout-btn" onClick={() => { logout(); setSidebarOpen(false); }}>
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    )}
                    <NavLink to="/privacy-policy" onClick={() => setSidebarOpen(false)} className="privacy-policy-link">Privacy Policy</NavLink>
                </div>
            </div>

            {/*Botón de barra lateral */}
            <button className="sidebar-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
                <FaBars size={24} />
            </button>

            {/* Iconos del header */}
            <div className="header-icons">
                {/* User Dropdown */}
                <div className="user-dropdown" ref={userDropdownRef}>
                    <button 
                        className="user-toggle"
                        onClick={() => setUserDropdownOpen(!userDropdownOpen)}
                    >
                        <FaUserCircle size={24} />
                    </button>

                    {userDropdownOpen && (
                        <div className={`user-dropdown-menu ${isAuthenticated ? "user-logged-in" : ""}`}>
                            {!isAuthenticated ? (
                                <>
                                    <NavLink 
                                        to="/user-login"
                                        className="user-login-link" 
                                        onClick={() => setUserDropdownOpen(false)}
                                    >
                                        Login
                                    </NavLink>
                                    <NavLink 
                                        to="/user-register"
                                        className="user-login-link" 
                                        onClick={() => setUserDropdownOpen(false)}
                                    >
                                        Register
                                    </NavLink>
                                </>
                            ) : (
                                <>
                                    <div className="user-info">
                                        <p className="logged-in-text">Logged in as</p>
                                        <p className="user-email">{userEmail}</p>
                                    </div>
                                    <NavLink to="/my-orders" className="user-option" onClick={() => setUserDropdownOpen(false)}>Order History</NavLink>
                                    <NavLink to="/password-change" className="user-option" onClick={() => setUserDropdownOpen(false)}>Change Password</NavLink>
                                    <button className="user-option" onClick={() => { logout(); setUserDropdownOpen(false); }}>Logout</button>
                                </>
                            )}
                        </div>
                    )}
                </div>

                {/* Carrito de compras */}
                <div className="cart-icon" ref={cartDropdownRef}>
                    <button className="cart-toggle" onClick={() => setCartDropdownOpen(!cartDropdownOpen)}>
                        <FaShoppingCart size={24} />
                    </button>

                    {cartDropdownOpen && (
                        <div className="cart-dropdown-menu">
                            {cartLoading && <p>Loading cart...</p>}
                            {cartError && <p className="error-text">{cartError}</p>}

                            {!cartLoading && !cartError && (
                                <>
                                    {cart.kind === "none" || cart.cart.items.length === 0 ? (
                                        <p className="empty-cart-text">Your cart is empty.</p>
                                    ) : (
                                        <div className="cart-pieces">
                                            <h3>Your Cart</h3>
                                            <div className="cart-pieces-table">
                                                <div className="cart-pieces-table-header">
                                                    <span className="cart-col-actions"></span>
                                                    <span className="cart-col-product">Product</span>
                                                    <span className="cart-col-price">Category</span>
                                                    <span className="cart-col-price">Price</span>
                                                </div>

                                                <ul className="cart-pieces-body">
                                                    {cart.kind === "user" ? cart.cart.items.map((item, index) => (
                                                        <li key={index} className="cart-pieces-row">
                                                            <div className="cart-col-actions">
                                                                <button
                                                                    type="button"
                                                                    title="Remove"
                                                                    onClick={() => handleRemoveFromCart(item.producto.id)}
                                                                >
                                                                    <FaTrash size={16} />
                                                                </button>
                                                            </div>

                                                            <div className="cart-col-product">
                                                                <div className="cart-product-info">
                                                                    <img
                                                                        src={item.imageUrl}
                                                                        alt={item.producto.nombre}
                                                                        className="cart-preview-image"
                                                                    />
                                                                    <span>{item.producto.nombre}</span>
                                                                </div>
                                                            </div>

                                                            <div className="cart-col-category">
                                                                {item.producto.nombreCategoria}
                                                            </div>
                                                            <div className="cart-col-price">
                                                                <span>{item.precioUnitario.toFixed(2)} €</span>
                                                            </div>
                                                        </li>
                                                    )) : cart.cart.items.map((item, index) => (
                                                        <li key={index} className="cart-pieces-row">
                                                            <div className="cart-col-actions">
                                                                <button
                                                                    type="button"
                                                                    title="Remove"
                                                                    onClick={() => handleRemoveFromCart(item.productId)}
                                                                >
                                                                    <FaTrash size={16} />
                                                                </button>
                                                            </div>

                                                            <div className="cart-col-product">
                                                                <div className="cart-product-info">
                                                                    <img
                                                                        src={item.imagen}
                                                                        alt={item.nombre}
                                                                        className="cart-preview-image"
                                                                    />
                                                                     <span>{item.nombre}</span>
                                                                </div>
                                                            </div>
                                                            <div className="cart-col-category">
                                                                {item.nombreCategoria}
                                                            </div>
                                                            <div className="cart-col-price">
                                                                {item.precio} €
                                                            </div>
                                                        </li>
                                                    ))}
                                                </ul>
                                            </div>
                                            {/*Botón de Checkout*/}
                                            <div className="cart-checkout-button-container">
                                                <NavLink
                                                    to="/cart"
                                                    className="cart-checkout-button"
                                                    onClick={() => setCartDropdownOpen(false)}
                                                >
                                                    Go to Cart
                                                </NavLink>
                                            </div>
                                        </div>
                                    )}
                                </>
                            )}
                        </div>
                    )}
                </div>

                {/* Icono de Instagram */}
                <a href="https://www.instagram.com/ceramic_affair/" target="_blank" rel="noopener noreferrer" className="instagram-icon">
                    <FaInstagram size={24} />
                </a>
            </div>

            {/* Overlay */}
            {sidebarOpen && <div className="overlay" onClick={() => setSidebarOpen(false)} />}
        </>
    );
}
// Este componente Header renderiza un encabezado con un logo y enlaces de navegación.
// Utiliza NavLink de react-router-dom para manejar la navegación interna y un enlace externo a Instagram.