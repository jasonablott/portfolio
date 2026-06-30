# ai_model_service/main.py
import io
import logging
from PIL import Image
from fastapi import FastAPI, File
from starlette.responses import JSONResponse
from transformers import pipeline

# -------------------------------------------------
# Setup
# -------------------------------------------------
logger = logging.getLogger("uvicorn.error")
logger.setLevel(logging.DEBUG)

device = "cpu"  # Force CPU since GPU/Metal may not support float64 weights

# run this path initially to download the model from huggingface
#model_path = "nlpconnect/vit-gpt2-image-captioning"

# once the model is downloaded, copy into model_weights folder for local re-use.
# model is stored here: ~/.cache/huggingface/hub/models--nlpconnect--vit-gpt2-image-captioning/snapshots/dc68f91c06a1ba6f15268e5b9c13ae7a7c514084
# copy that snapshot to model_weights using:
# cp -r ~/.cache/huggingface/hub/models--nlpconnect--vit-gpt2-image-captioning/snapshots/dc68f91c06a1ba6f15268e5b9c13ae7a7c514084 ./model_weights/vit-gpt2-image-captioning
model_path = "model_weights/vit-gpt2-image-captioning"

print("Loading image captioning model from:", model_path)
captioner = pipeline("image-to-text", model=model_path, device=-1)
print("Model loaded successfully")

# -------------------------------------------------
# FastAPI App
# -------------------------------------------------
app = FastAPI(title="AI Image Captioning Service")


@app.get("/")
async def root():
    return {"message": "AI Image Captioning Service is running"}

#Receives an image file and returns a generated caption.
@app.post("/analyze")
async def analyze_image(image: bytes = File(...)):

    logger.debug("Received image for analysis")

    # Convert uploaded bytes to PIL image
    raw_image = Image.open(io.BytesIO(image)).convert("RGB")

    # Run caption generation
    result = captioner(raw_image)
    caption = result[0]["generated_text"]

    logger.info(f"Generated caption: {caption}")
    return JSONResponse(content={"caption": caption})

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True) # listen on pert 8000


