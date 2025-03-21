<template>
    <div class="image-container">
        <div class="canvas">

            <div @wheel="handleZoom" class="zoom-container">
                <div ref="imageWrapper" class="image-wrapper">

                    <img :src="image" alt="ID Photo" class="photo" />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        image: {
            type: String,
            //image is passed as prop from above
            // default: "/assets/portrait.jpg" // Default value if no prop is passed
        }
    },

    data() {
        return {
            scale: 1, // Initial zoom level (scale)
        };
    },
    methods: {
        // Handle the zoom event (Ctrl + Scroll)
        handleZoom(event) {
            // Only zoom when the Ctrl key is pressed
            if (event.ctrlKey) {
                event.preventDefault(); // Prevent the default browser zoom

                // Calculate the zoom factor (zoom in or out)
                const delta = event.deltaY || event.detail || event.wheelDelta;
                const zoomFactor = delta < 0 ? 1.1 : 0.9; // Zoom in when scrolling up, zoom out when scrolling down

                // Update the scale (zoom level)
                this.scale *= zoomFactor;

                // Limit zoom level to avoid extreme zoom
                this.scale = Math.min(Math.max(this.scale, 0.5), 3);

                // Call zoomImage function to zoom into the mouse position
                this.zoomImage(event);
            }
        },
        // Zoom the image based on the mouse position
        zoomImage(event) {
            const imageWrapper = this.$refs.imageWrapper;
            const rect = imageWrapper.getBoundingClientRect(); // Get image wrapper dimensions
            const offsetX = event.clientX - rect.left; // X offset of mouse
            const offsetY = event.clientY - rect.top; // Y offset of mouse

            // Set the zoom origin based on mouse position
            imageWrapper.style.transformOrigin = `${(offsetX / rect.width) * 100}% ${(offsetY / rect.height) * 100}%`;
            imageWrapper.style.transform = `scale(${this.scale})`; // Apply zoom
        },
    },
}
</script>

<style scoped>
.image-container {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #121416;
    overflow: auto;
}

.canvas {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
}

.photo {
    display: block;
    max-width: 90%;
    max-height: 80vh;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);

    /* width: 100%;
    height: 100%; */
    object-fit: contain;
    transition: transform 0.2s ease-in-out;

}

.zoom-container {
    position: relative;
    width: 100%;
    height: 100%;
    /* overflow: hidden; */
}

.image-wrapper {
    transition: transform 0.1s ease;
    width: 100%;
    height: 100%;
}
</style>