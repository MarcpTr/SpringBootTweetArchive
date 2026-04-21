const errorMessage = document.getElementById('error-message');
const successMessage = document.getElementById('success-message');
function deleteCollection(collectionId) {

    if (!confirm("¿Seguro que quieres eliminar esta colección?")) {
        return;
    }

    fetch(`/api/collection/${collectionId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        }
    })
        .then(async response => {
            const data = await response.json();
            if (!response.ok) throw data;
            return data;
        })
        .then(data => {

            console.log(data.message);

            // 🔥 eliminar del DOM sin reload
            const card = document.getElementById("collection-" + collectionId);
            if (card) {
                card.remove();
            }

        })
        .catch(err => {
            alert(err.message || "Error eliminando colección");
            console.error(err);
        });
}
function toggleVisibility(collectionId) {

    fetch(`/api/collection/${collectionId}/visibility`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken

        }
    })
        .then(async response => {
            const data = await response.json();
            if (!response.ok) throw data;
            return data;
        })
        .then(data => {

            // 🔥 actualizar icono
            const btn = document.getElementById("btn-" + collectionId);
            const icon = btn.querySelector("i");

            const isNowPublic = icon.classList.contains("fa-eye-slash");

            icon.classList.toggle("fa-eye");
            icon.classList.toggle("fa-eye-slash");

            console.log(data.message);

        })
        .catch(err => {
            alert(err.message || "Error cambiando visibilidad");
            console.error(err);
        });
}