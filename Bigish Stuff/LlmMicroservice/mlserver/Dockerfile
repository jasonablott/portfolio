FROM python:3.13.9-slim
# start with a stripped down distribution with only python installed

LABEL authors="ben"

# install packages that are required
RUN pip3 install fastapi[standard] numpy torchvision transformers

# mkdir/cd in the container
WORKDIR /app

# copy stuff from my development folder into the container image
COPY main.py /app/main.py
COPY model_weights /app/model_weights

# let this container access port 8000 when running
EXPOSE 8000

# This is what runs when you start the container.  The pwd && ls && were a hacky way to
# do some debugging
CMD pwd && ls && fastapi --verbose run main.py

