 const loadingLikes = new Set();

        async function toggleLike(collectionId) {
            if (loadingLikes.has(collectionId)) return;

            const button = document.querySelector(
                `.like-btn[onclick="event.stopPropagation(); toggleLike(${collectionId});"]`
            );

            if (!button) return;

            const countSpan = button.querySelector(".like-count");

            let liked = button.classList.contains("liked");
            let likes = parseInt(countSpan.textContent || "0");

            loadingLikes.add(collectionId);

            try {
                if (!liked) {
                    await fetch(`/api/collection/${collectionId}/like`, {
                        method: "POST",
                        headers: getHeaders()
                    });

                    liked = true;
                    likes++;

                    button.classList.add("liked");
                    button.innerHTML = `❤️ <span class="like-count">${likes}</span>`;
                } else {
                    await fetch(`/api/collection/${collectionId}/like`, {
                        method: "DELETE",
                        headers: {
                            'Content-Type': 'application/json',
                            [csrfHeader]: csrfToken
                        }
                    });

                    liked = false;
                    likes--;

                    button.classList.remove("liked");
                    button.innerHTML = `🤍 <span class="like-count">${likes}</span>`;
                }
            } catch (err) {
                console.error("Error toggle like:", err);
            }

            loadingLikes.delete(collectionId);
        }