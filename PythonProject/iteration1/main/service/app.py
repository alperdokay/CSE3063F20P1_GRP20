from flask import Flask
from flask import jsonify, request
from flask_cors import CORS
app = Flask(__name__)

CORS(app)
@app.route('/login', methods=['POST'])
def login():
    # Credentials check

    return jsonify({'response': 'true', 'code': 200})