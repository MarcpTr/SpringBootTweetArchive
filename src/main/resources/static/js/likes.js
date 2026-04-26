const loadingLikes = new Set();

async function toggleLike(collectionId, button) {
    if (loadingLikes.has(collectionId)) return;

    const countSpan = button.querySelector(".like-count");
    let liked = button.classList.contains("liked");
    let likes = parseInt(countSpan.textContent || "0", 10);

    // Optimistic UI
    liked = !liked;
    likes += liked ? 1 : -1;

    updateButton(button, liked, likes);

    loadingLikes.add(collectionId);
    button.disabled = true;

    try {
        const response = await fetch(`/api/collection/${collectionId}/like`, {
            method: liked ? "POST" : "DELETE",
            headers: getHeaders()
        });

        if (!response.ok) {
            throw new Error("Request failed");
        }
    } catch (err) {
        console.error("Error toggle like:", err);

        // rollback UI
        liked = !liked;
        likes += liked ? 1 : -1;
        updateButton(button, liked, likes);
    } finally {
        loadingLikes.delete(collectionId);
        button.disabled = false;
    }
}

function updateButton(button, liked, likes) {
    button.classList.toggle("liked", liked);

    const iconSpan = button.querySelector(".like-icon");
    const countSpan = button.querySelector(".like-count");

    if (iconSpan) {
        iconSpan.textContent = liked ? "❤️" : "🤍";
    }

    if (countSpan) {
        countSpan.textContent = likes;
    }

    // 🔥 NUEVO: feedback visual
    button.classList.add("scale-110");
    setTimeout(() => button.classList.remove("scale-110"), 150);
}