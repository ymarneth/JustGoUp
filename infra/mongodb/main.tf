resource "helm_release" "mongodb" {
  name      = var.release_name
  namespace = var.namespace

  chart = "oci://registry-1.docker.io/bitnamicharts/mongodb"

  values = [
    yamlencode({
      architecture = "standalone"

      auth = {
        enabled      = true
        rootUser     = var.mongodb_root_user
        rootPassword = var.mongodb_root_password

        username = var.mongodb_app_user
        password = var.mongodb_app_password
        database = var.mongodb_app_db
      }

      persistence = {
        enabled = true
        size    = var.persistence_size
      }
    })
  ]
}

resource "kubernetes_job_v1" "seed_data" {
  metadata {
    name      = "mongodb-seed"
    namespace = var.namespace
  }

  spec {
    backoff_limit = 0

    template {
      metadata {
        labels = { app = "mongodb-seed" }
      }

      spec {
        restart_policy = "Never"

        container {
          name  = "seed-mongo-data"
          image = "mongo:8.2.3"

          env {
            name  = "MONGO_HOST"
            value = "${var.release_name}.${var.namespace}.svc.cluster.local"
          }
          env { 
            name = "MONGO_PORT"
            value = "27017"
          }
          env {
            name = "MONGO_USER"
            value = var.mongodb_app_user
          }
          env {
            name = "MONGO_PASS"
            value = var.mongodb_app_password
          }
          env { 
            name = "MONGO_DB"
            value = var.mongodb_app_db
          }

          command = [
            "mongosh",
            "mongodb://$(MONGO_USER):$(MONGO_PASS)@$(MONGO_HOST):$(MONGO_PORT)/$(MONGO_DB)?authSource=$(MONGO_DB)",
            "--eval",
            <<-EOT
              const dbName = process.env.MONGO_DB;
              const db = db.getSiblingDB(dbName);

              // wipe for local dev
              db.climbing_sessions.deleteMany({});

              // Session 1 (climbing_session + boulders)
              db.climbing_sessions.insertOne({
                _id: "sess_001",
                location: "BoulderHaus Wien",
                startTime: new Date("2026-01-10T18:30:00Z"),
                notes: "Power session. Felt strong on pinches.",
                boulders: [
                  {
                    _id: "b_003",
                    sessionId: "sess_001",
                    gradeType: "Font",
                    gradeValue: "6C",
                    attempts: 6,
                    sent: 0,
                    flash: 0,
                    repeated: 0,
                    color: "red",
                    notes: "Could not stick the dyno.",
                    createdAt: new Date("2026-01-10T19:55:00Z")
                  },
                  {
                    _id: "b_002",
                    sessionId: "sess_001",
                    gradeType: "Font",
                    gradeValue: "6A+",
                    attempts: 1,
                    sent: 1,
                    flash: 1,
                    repeated: 0,
                    color: "green",
                    notes: "Nice flow.",
                    createdAt: new Date("2026-01-10T19:25:00Z")
                  },
                  {
                    _id: "b_001",
                    sessionId: "sess_001",
                    gradeType: "Font",
                    gradeValue: "6B",
                    attempts: 3,
                    sent: 1,
                    flash: 0,
                    repeated: 0,
                    color: "blue",
                    notes: "Hard move in the middle.",
                    createdAt: new Date("2026-01-10T19:05:00Z")
                  }
                ]
              });

              // Session 2
              db.climbing_sessions.insertOne({
                _id: "sess_002",
                location: "ClimbFactory",
                startTime: new Date("2026-01-12T19:00:00Z"),
                notes: "Endurance session.",
                boulders: [
                  {
                    _id: "b_005",
                    sessionId: "sess_002",
                    gradeType: "V-Scale",
                    gradeValue: "V2",
                    attempts: 1,
                    sent: 1,
                    flash: 0,
                    repeated: 1,
                    color: "yellow",
                    notes: "Repeat for volume.",
                    createdAt: new Date("2026-01-12T19:55:00Z")
                  },
                  {
                    _id: "b_004",
                    sessionId: "sess_002",
                    gradeType: "V-Scale",
                    gradeValue: "V3",
                    attempts: 2,
                    sent: 1,
                    flash: 0,
                    repeated: 0,
                    color: "black",
                    notes: "Felt pumpy.",
                    createdAt: new Date("2026-01-12T19:35:00Z")
                  }
                ]
              });

              // indexes matching your SQL query patterns
              db.climbing_sessions.createIndex({ startTime: -1 });
              db.climbing_sessions.createIndex({ "boulders._id": 1 });
              db.climbing_sessions.createIndex({ location: 1, startTime: -1 });

              print("Seeded climbing_sessions + embedded boulders.");
            EOT
          ]
        }
      }
    }
  }

  depends_on = [helm_release.mongodb]
}