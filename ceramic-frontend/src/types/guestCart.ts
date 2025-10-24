import type { ProductoOutputDTO } from "./producto.types";

const CART_KEY = 'guest_cart';

export interface GuestCartItem {
    productId: number;
    nombre: string;
    precio: number;
    nombreCategoria: string;
    imagen: string;
}

export interface GuestCart {
    items: GuestCartItem[];
}

function getCart(): GuestCart {
    const cartJson = localStorage.getItem(CART_KEY);
    return cartJson ? JSON.parse(cartJson) : { items: [] };
}

function saveCart(cart: GuestCart): void {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
}

export function addItemToGuestCart(product: ProductoOutputDTO, imageUrl: string) {
    const cart = getCart();

    // Evitar duplicados
    if (!cart.items.some(item => item.productId === product.id)) {
        cart.items.push({
            productId: product.id,
            nombre: product.nombre,
            precio: product.precio,
            nombreCategoria: product.nombreCategoria,
            imagen: imageUrl
        });
        saveCart(cart);
    }
}

export function removeItemFromGuestCart(productId: number) {
    const cart = getCart();
    cart.items = cart.items.filter(item => item.productId !== productId);
    saveCart(cart);
}

export function clearGuestCart() {
    localStorage.removeItem(CART_KEY);
}

export function getGuestCart(): GuestCart {
    return getCart();
}