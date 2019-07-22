INSERT INTO collaboration_group(name)  SELECT 'Test group' WHERE NOT EXISTS ( SELECT 1 FROM collaboration_group WHERE name='Test group');
