resource "kubernetes_namespace" "ns" {
  metadata {
    name = var.namespace
  }
}

resource "helm_release" "mongodb" {
  name      = var.release_name
  namespace = var.namespace

  repository = "https://charts.bitnami.com/bitnami"
  chart      = "mongodb"
  # version  = "15.6.2"  # optional: comment out if it causes issues

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
        # Only set storageClass if provided
        storageClass = var.storage_class_name
      }

      image = {
        debug = false
      }
    })
  ]
}


#
# Seed data Job (runs once). If you re-apply and want it to run again,
# change the job name (e.g., add a suffix) or delete the job in cluster.
#
resource "kubernetes_job" "seed_data" {
  metadata {
    name      = "mongodb-seed"
    namespace = var.namespace
  }

  spec {
    backoff_limit = 1

    template {
      metadata {
        labels = { app = "mongodb-seed" }
      }

      spec {
        restart_policy = "Never"

        container {
          name  = "seed"
          image = "bitnami/mongodb:7.0"

          env {
            name  = "MONGO_HOST"
            value = "${helm_release.mongodb.name}.${var.namespace}.svc.cluster.local"
          }
          env {
            name  = "MONGO_PORT"
            value = "27017"
          }
          env {
            name  = "MONGO_USER"
            value = var.mongodb_app_user
          }
          env {
            name  = "MONGO_PASS"
            value = var.mongodb_app_password
          }
          env {
            name  = "MONGO_DB"
            value = var.mongodb_app_db
          }

          command = ["/bin/bash", "-lc"]
          args = [
            <<-EOT
              set -euo pipefail
              echo "Waiting for MongoDB..."
              for i in {1..60}; do
                mongosh "mongodb://$MONGO_USER:$MONGO_PASS@$MONGO_HOST:$MONGO_PORT/$MONGO_DB?authSource=$MONGO_DB" --eval "db.runCommand({ ping: 1 })" >/dev/null 2>&1 && break
                sleep 2
              done

              echo "Seeding example data..."

              mongosh "mongodb://$MONGO_USER:$MONGO_PASS@$MONGO_HOST:$MONGO_PORT/$MONGO_DB?authSource=$MONGO_DB" <<'MONGO'
              db = db.getSiblingDB("justgoup");

              db.users.insertMany([
                { _id: "u1", name: "Alex", createdAt: new Date(), gym: "BoulderHaus" },
                { _id: "u2", name: "Sam",  createdAt: new Date(), gym: "ClimbFactory" }
              ]);

              db.sessions.insertMany([
                {
                  userId: "u1",
                  date: new Date("2026-01-10T18:30:00Z"),
                  gym: "BoulderHaus",
                  gradingSystem: "Font",
                  notes: "Power session",
                  sends: 4,
                  flashes: 1
                },
                {
                  userId: "u2",
                  date: new Date("2026-01-12T19:00:00Z"),
                  gym: "ClimbFactory",
                  gradingSystem: "V-Scale",
                  notes: "Endurance",
                  sends: 6,
                  flashes: 0
                }
              ]);

              db.ascents.insertMany([
                { userId: "u1", sessionDate: new Date("2026-01-10T18:30:00Z"), routeName: "Blue Arete", grade: "6B", attempts: 3, sendType: "send" },
                { userId: "u1", sessionDate: new Date("2026-01-10T18:30:00Z"), routeName: "Sloper Party", grade: "6A+", attempts: 1, sendType: "flash" },
                { userId: "u2", sessionDate: new Date("2026-01-12T19:00:00Z"), routeName: "Roof Problem", grade: "V3", attempts: 2, sendType: "send" }
              ]);

              db.users.createIndex({ name: 1 });
              db.sessions.createIndex({ userId: 1, date: -1 });
              db.ascents.createIndex({ userId: 1, sessionDate: -1 });

              print("Done.");
              MONGO
            EOT
          ]
        }
      }
    }
  }

  depends_on = [helm_release.mongodb]
}
