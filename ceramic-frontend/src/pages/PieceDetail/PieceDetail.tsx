import { Link, useParams } from "react-router-dom";
import { useCallback, useEffect, useRef, useState } from "react";
import { getProductoById, deleteProducto } from "../../api/productos";
import type { ProductoOutputDTO } from "../../types/producto.types";
import type { Imagen } from "../../types/imagen.types";
import { getImagenById } from "../../api/imagenes";
import { FaChevronLeft, FaChevronRight, FaEdit, FaShoppingCart, FaTrash } from "react-icons/fa";
import "./PieceDetail.css";
import ZoomImage from "../../components/ZoomImage/ZoomImage";
import ImageModal from "../../components/ImageModal/ImageModal";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useSwipeable } from "react-swipeable";
import { addItemToCart } from "../../api/carrito";
import { addItemToGuestCart, getGuestCart } from "../../types/guestCart";

const BASE_IMAGE_URL = "http://localhost:8080/uploads/";

export default function PieceDetail() {
    const navigate = useNavigate();
    const { id } = useParams();
    const [producto, setProducto] = useState<ProductoOutputDTO | null>(null);
    const [imagenes, setImagenes] = useState<Imagen[]>([]);
    const [showModal, setShowModal] = useState(false);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const { hasRole, isAuthenticated, userId } = useAuth(); // Hook de autenticación
    const [isMobile, setIsMobile] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [addingToCart, setAddingToCart] = useState(false);
    const [cartMessage, setCartMessage] = useState<string | null>(null);
    const [visibleMessage, setVisibleMessage] = useState(false);

    const thumbsRef = useRef<HTMLDivElement>(null);

    // Efecto para centrar la miniatura activa
    useEffect(() => {
        if (!thumbsRef.current) return;

        const activeThumb = thumbsRef.current.querySelector<HTMLImageElement>(
            ".thumb.active"
        );
        if (activeThumb) {
            // Scroll horizontal para que la miniatura activa sea visible y centrada aproximadamente
            const container = thumbsRef.current;
            const containerRect = container.getBoundingClientRect();
            const thumbRect = activeThumb.getBoundingClientRect();

            const offset =
                thumbRect.left - containerRect.left - container.clientWidth / 2 + thumbRect.width / 2;

            container.scrollBy({
            left: offset,
            behavior: "smooth",
            });
        }
    }, [currentImageIndex]);


    // Efecto para obtener el producto y sus imágenes al cargar el componente
    useEffect(() => {
        const fetchProducto = async () => {
            if (id) {
                try {
                    const data = await getProductoById(Number(id));
                    setProducto(data);

                    // Obtener las imágenes asociadas al producto
                    const imagePromises = await Promise.all(
                        data.idsImagenes.map((imageId) => getImagenById(imageId))
                    );
                    setImagenes(imagePromises);
                } catch (error) {
                    console.error("Error fetching product:", error);
                }
            }
        };
        fetchProducto();
    }, [id]);

    // Efecto para detectar si es móvil
    useEffect(() => {
        const handleResize = () => {
            setIsMobile(window.innerWidth < 768);
        };

        handleResize(); // Verificar el tamaño inicial
        window.addEventListener("resize", handleResize);

        return () => {
            window.removeEventListener("resize", handleResize);
        };
    }, []);

    // Efecto para mostrar/ocultar el mensaje de carrito
    useEffect(() => {
        if (cartMessage) {
            setVisibleMessage(true);

            // Auto-cierre después de 3 segundos
            const timer = setTimeout(() => setVisibleMessage(false), 3000);

            return () => clearTimeout(timer);
        }
    }, [cartMessage]);

    // Cuando termina la animación, limpia el cartMessage
    const handleTransitionEnd = () => {
        if (!visibleMessage) setCartMessage(null);
    };

    // Configuración de swipe para las miniaturas
    const handlers = useSwipeable({
        onSwipedLeft: () => {
            if (thumbsRef.current) {
                thumbsRef.current.scrollBy({ left: 70, behavior: "smooth" });
            }
        },
        onSwipedRight: () => {
            if (thumbsRef.current) {
                thumbsRef.current.scrollBy({ left: -70, behavior: "smooth" });
            }
        },
        trackMouse: true,
    });

    const { ref: swipeRef, ...handlersWithoutRef } = handlers;

    // Ref combinado para las miniaturas
    const combinedRef = useCallback((node: HTMLDivElement | null) => {
        thumbsRef.current = node;

        if (typeof swipeRef === "function") {
            swipeRef(node);
        } else if (swipeRef && "current" in swipeRef) {
            (swipeRef as React.RefObject<HTMLDivElement | null>).current = node;
        }
    }, [swipeRef]);

    if (!producto) {
        return <p>Loading...</p>;
    }

    // Manejadores de eventos
    const handlePrev = () => {
        setCurrentImageIndex((prevIndex) => 
            (prevIndex === 0 ? producto.idsImagenes.length - 1 : prevIndex - 1)
        );
    };

    const handleNext = () => {
        setCurrentImageIndex((prevIndex) => 
            (prevIndex === producto.idsImagenes.length - 1 ? 0 : prevIndex + 1)
        );
    };

    const handleEditClick = () => {
        if (producto) {
            navigate(`/admin/products/edit/${producto.id}`);
        }
    };

    const handleDeleteClick = async () => {
        if (!producto) return;

        setIsDeleting(true);

        try {
            await deleteProducto(producto.id);
            navigate("/pieces");
        } catch (error) {
            console.error("Error deleting product:", error);
            setIsDeleting(false);
        } finally {
            setIsDeleting(false);
            setShowDeleteModal(false);
        }
    };

    const handleOpenDeleteModal = () => {
        setShowDeleteModal(true);
    }

    const handleCloseDeleteModal = () => {
        setShowDeleteModal(false);
    }

    // Manejar la adición al carrito
    const handleAddToCart = async () => {
        if (!producto) return;

        setAddingToCart(true);
        setCartMessage(null);

        try {
            if (isAuthenticated && userId) {
                // Usuario loguado -> usar API del backend
                await addItemToCart(userId.toString(), producto.id.toString());
                setCartMessage("Piece added to cart!");
            } else {
                // Usuario invitado -> usar localStorage
                const imageUrl = 
                    imagenes.length > 0
                    ? `${BASE_IMAGE_URL}${imagenes[0].ruta}`
                    : "/images/1068302.png";
                
                const { items: guestCartItems } = getGuestCart();

                const alreadyInCart = guestCartItems.some(
                    (item: any) => item.productId === producto.id
                );

                if (alreadyInCart) {
                    setCartMessage("This piece is already in your cart!");
                } else {
                    addItemToGuestCart(producto, imageUrl);
                    setCartMessage("Piece added to cart!");
                }
            }
        } catch (error: any) {
            console.error("Error adding item to cart:", error);
            
            if (error instanceof Error && error.message.includes("Product already in cart")) {
                setCartMessage("This piece is already in your cart!");
            } else {
                setCartMessage("Error adding piece to cart. Please contact support.");
            }
        } finally {
            setAddingToCart(false);
        }
    };

    return (
        <div className="piece-detail-container">

            {/* Breadcrumb */}
            <div className="breadcrumb">
                <Link to="/pieces">Pieces</Link> &gt;{" "}
                <Link 
                    to={
                        producto.idCategoria
                        ? `/pieces?categoria=${encodeURIComponent(producto.idCategoria)}&showFilters=true`
                        : `/pieces?showFilters=true`
                    }
                    >
                        <span>{producto.nombreCategoria || "Uncategorized"}</span>
                </Link> &gt;
                <span> {producto.nombre}</span>
            </div>

            <div className="piece-detail-content">

                {/* Galería */}
                <div className="gallery">
                    {imagenes.length > 0 ? (
                        <>
                            <div className="gallery-main">
                                <ZoomImage
                                    src={`${BASE_IMAGE_URL}${imagenes[currentImageIndex].ruta}`}
                                    alt={producto.nombre}
                                    onDoubleClick={() => !isMobile && setShowModal(true)}
                                    onClick={() => isMobile && setShowModal(true)}
                                    enableZoom={!isMobile}
                                />
                                {showModal && (
                                    <ImageModal
                                        images={imagenes}
                                        currentIndex={currentImageIndex}
                                        alt={producto.nombre}
                                        onClose={() => setShowModal(false)}
                                    />
                                )}
                            </div>
                            <div className="thumbs-container">

                                {/* Miniaturas */}
                                <div className="gallery-thumbs" ref={combinedRef} {...handlersWithoutRef}>
                                    {imagenes.map((imagen, index) => (
                                        <img
                                        key={imagen.id}
                                        src={`${BASE_IMAGE_URL}${imagen.ruta}`}
                                        alt={`${producto.nombre} thumbnail`}
                                        className={`thumb ${index === currentImageIndex ? "active" : ""}`}
                                        onClick={() => setCurrentImageIndex(index)}
                                        />
                                    ))}
                                </div>

                                {/* Flechas de navegación */}
                                {imagenes.length > 1 && (
                                <>
                                    <button className="thumb-nav-btn left" onClick={handlePrev}><FaChevronLeft /></button>
                                    <button className="thumb-nav-btn right" onClick={handleNext}><FaChevronRight /></button>
                                </>
                                )}
                            </div>
                        </>
                    ) : (
                        <div className="gallery-main">
                            <img
                                src="/images/1068302.png"
                                alt="Default"
                                className="default-image"
                            />
                        </div>
                    )}
                </div>

                {/* Detalles del producto */}
                <div className="details">

                    {/* Nombre y precio */}
                    <div className="product-header">
                        <h2 className="product-name">{producto.nombre}</h2>
                        <h2 className="product-price">{producto.precio.toFixed(2)}€</h2>
                    </div>

                    {/* Descripción */}
                    <p className="product-description">{producto.descripcion}</p>
                    
                    {/* Tamaños */}
                    <div className="sizes">
                        <h3>Sizes</h3>
                        {producto.altura > 0 && <p>Height: {producto.altura} cm</p>}
                        {producto.anchura > 0 && <p>Width: {producto.anchura} cm</p>}
                        {producto.diametro > 0 && <p>Diameter: {producto.diametro} cm</p>}
                    </div>

                    {/* Sold out o añadir al carrito */}
                    {producto.soldOut ? (
                        <div className="sold-out">
                            <span>SOLD OUT</span>
                        </div>
                    ) : (
                        <div className="add-to-cart">
                            <button
                                onClick={handleAddToCart}
                                disabled={addingToCart}
                                className="edit-button"
                            >
                                {addingToCart ? "Adding..." : <><FaShoppingCart /> Add to Cart</>}
                            </button>
                        </div>
                    )}

                    {/* Botón de editar (solo para administradores) */}
                    {hasRole("ADMIN") && (
                        <div className="admin-actions">
                            <button 
                                className="edit-button"
                                onClick={handleEditClick}
                            >
                                <FaEdit /> Edit
                            </button>

                            <div className="piece-delete-container">
                                <button 
                                    className="delete-button"
                                    onClick={handleOpenDeleteModal}
                                >
                                    <FaTrash /> Delete
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
            
            {/* Modal de confirmación de eliminación */}
            {showDeleteModal && (
                <div className="delete-modal-overlay" onClick={handleCloseDeleteModal}>
                    <div 
                    className="delete-modal" 
                    onClick={(e) => e.stopPropagation()}
                    >
                    <h3>Confirm Deletion</h3>
                    <p>
                        Are you sure you want to delete <strong>{producto.nombre}</strong>? 
                        This action cannot be undone.
                    </p>

                    <div className="delete-modal-actions">
                        <button 
                        onClick={handleCloseDeleteModal} 
                        className="modal-btn cancel"
                        disabled={isDeleting}
                        >
                        Cancel
                        </button>
                        <button 
                        onClick={handleDeleteClick} 
                        className="modal-btn confirm"
                        disabled={isDeleting}
                        >
                        {isDeleting ? "Deleting..." : "Confirm Delete"}
                        </button>
                    </div>
                    </div>
                </div>
            )}

            {/* Mensaje tipo toast para el carrito */}
            {cartMessage && (
                <div
                    className={`toast-message ${visibleMessage ? "show" : "hide"}`}
                    onTransitionEnd={handleTransitionEnd}
                >
                    {cartMessage}
                    <button
                        className="toast-close-btn"
                        onClick={() => setVisibleMessage(false)}
                    >
                        ×
                    </button>
                </div>
            )}
        </div>
    );
}