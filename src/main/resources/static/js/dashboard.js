
const errorMessage = document.getElementById('error-message');
const successMessage = document.getElementById('success-message');

// helper para no repetir lógica
function getHeaders() {
    const headers = {
        'Content-Type': 'application/json'
    };

    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    return headers;
}

async function deleteCollection(collectionId) {

    if (!confirm("¿Seguro que quieres eliminar esta colección?")) {
        return;
    }

    try {
        const response = await fetch(`/api/collection/${collectionId}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        const data = await response.json();
        if (!response.ok) throw data;

        console.log(data.message);

        // 🔥 eliminar del DOM sin reload
        const card = document.getElementById("collection-" + collectionId);
        if (card) {
            card.remove();
        }

    } catch (err) {
        alert(err.message || "Error eliminando colección");
        console.error(err);
    }
}

async function toggleVisibility(collectionId) {

    try {
        const response = await fetch(`/api/collection/${collectionId}/visibility`, {
            method: 'PUT',
            headers: getHeaders()
        });

        const data = await response.json();
        if (!response.ok) throw data;

        const btn = document.getElementById("btn-" + collectionId);
        const icon = btn.querySelector("i");

        const isNowPublic = icon.classList.contains("fa-eye-slash");

        icon.classList.toggle("fa-eye");
        icon.classList.toggle("fa-eye-slash");

        console.log(data.message);

    } catch (err) {
        alert(err.message || "Error cambiando visibilidad");
        console.error(err);
    }
}