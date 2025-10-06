CREATE TABLE IF NOT EXISTS error_log (
  id BIGSERIAL PRIMARY KEY,
  timestamp TIMESTAMP NOT NULL,
  service_name VARCHAR(100) NOT NULL,
  method_signature VARCHAR(500) NOT NULL,
  exception_message TEXT,
  stack_trace TEXT,
  method_parameters TEXT,
  log_level VARCHAR(10) NOT NULL CHECK (log_level IN ('ERROR', 'WARNING', 'INFO')),
  description VARCHAR(1000)
);
